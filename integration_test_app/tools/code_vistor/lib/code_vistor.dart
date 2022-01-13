import 'dart:typed_data';

import 'package:analyzer/dart/analysis/analysis_context.dart';
import 'package:analyzer/dart/ast/ast.dart';
import 'package:analyzer/dart/element/type.dart';
import 'package:analyzer/dart/element/type_visitor.dart';
import 'package:path/path.dart' as path;
import 'package:analyzer/dart/analysis/analysis_context_collection.dart'
    show AnalysisContextCollection;
import 'dart:convert';
import 'dart:io';
import 'package:file/file.dart' as file;

import 'package:analyzer/dart/analysis/results.dart' show ParsedUnitResult;
import 'package:analyzer/dart/analysis/session.dart' show AnalysisSession;
import 'package:analyzer/dart/ast/ast.dart' as dart_ast;
import 'package:analyzer/dart/ast/syntactic_entity.dart'
    as dart_ast_syntactic_entity;
import 'package:analyzer/dart/ast/visitor.dart' as dart_ast_visitor;
import 'package:analyzer/error/error.dart' show AnalysisError;
import 'package:file/local.dart';

class CallApiInvoke {
  late String apiType;
  late String params;
}

class FunctionBody {
  late CallApiInvoke callApiInvoke;
}

class Parameter {
  late DartType? dartType;
  late String type;
  List<String> typeArguments = [];
  late String name;
  late bool isNamed;
  late bool isOptional;
  String? defaultValue;
}

extension ParameterExt on Parameter {
  bool get isPrimitiveType =>
      type == 'int' ||
      type == 'double' ||
      type == 'bool' ||
      type == 'String' ||
      type == 'List' ||
      type == 'Map' ||
      type == 'Set';

  String primitiveDefualtValue() {
    switch (type) {
      case 'int':
        return '10';
      case 'double':
        return '10.0';
      case 'String':
        return '"hello"';
      case 'bool':
        return 'true';
      case 'List':
        return '[]';
      case 'Map':
        return '{}';
      case 'Uint8List':
        return 'Uint8List.fromList([])';
      case 'Set':
        return '{}';
      default:
        throw Exception('not support type $type');
    }
  }
}

class Type {
  late String type;
  List<String> typeArguments = [];
}

extension TypeExt on Type {
  bool get isPrimitiveType =>
      type == 'int' ||
      type == 'double' ||
      type == 'bool' ||
      type == 'String' ||
      type == 'List' ||
      type == 'Map' ||
      type == 'Set';

  String primitiveDefualtValue() {
    switch (type) {
      case 'int':
        return '10';
      case 'double':
        return '10.0';
      case 'String':
        return '"hello"';
      case 'bool':
        return 'true';
      case 'List':
        return '[]';
      case 'Map':
        return '{}';
      case 'Uint8List':
        return 'Uint8List.fromList([])';
      case 'Set':
        return '{}';
      default:
        throw Exception('not support type $type');
    }
  }

  bool isVoid() {
    return type == 'void';
  }
}

class SimpleLiteral {
  late String type;
  late String value;
}

class SimpleAnnotation {
  late String name;
  List<SimpleLiteral> arguments = [];
}

class SimpleComment {
  List<String> commentLines = [];
  late int offset;
  late int end;
}

class BaseNode {
  late SimpleComment comment;
}

class Method extends BaseNode {
  late String name;
  late FunctionBody body;
  List<Parameter> parameters = [];
  late Type returnType;
}

class Field extends BaseNode {
  late Type type;
  late String name;
}

class Constructor extends BaseNode {
  late String name;
  List<Parameter> parameters = [];
  late bool isFactory;
}

class Clazz extends BaseNode {
  late String name;
  List<Constructor> constructors = [];
  List<Method> methods = [];
  List<Field> fields = [];
}

class EnumConstant extends BaseNode {
  late String name;
  List<SimpleAnnotation> annotations = [];
}

class Enumz extends BaseNode {
  late String name;
  List<EnumConstant> enumConstants = [];
}

class ParseResult {
  late Map<String, Clazz> classMap;
  late Map<String, Enumz> enumMap;

  // TODO(littlegnal): Optimize this later.
  // late Map<String, List<String>> classFieldsMap;
  // late Map<String, String> fieldsTypeMap;
  late Map<String, List<String>> genericTypeAliasParametersMap;
}

class DefaultVisitor extends dart_ast_visitor.RecursiveAstVisitor<Object?> {
  final classFieldsMap = <String, List<String>>{};
  final fieldsTypeMap = <String, String>{};
  final genericTypeAliasParametersMap = <String, List<String>>{};

  final classMap = <String, Clazz>{};
  final enumMap = <String, Enumz>{};

  @override
  Object? visitFieldDeclaration(dart_ast.FieldDeclaration node) {
    stdout.writeln(
        'variables: ${node.fields.variables}, type: ${node.fields.type}');

    final clazz = _getClazz(node);
    if (clazz == null) return null;

    final dart_ast.TypeAnnotation? type = node.fields.type;
    if (type is dart_ast.NamedType) {
      final fieldName = node.fields.variables[0].name.name;

      Field field = Field()
        ..name = fieldName
        ..comment = _generateComment(node);

      // if (node.parent is dart_ast.ClassDeclaration) {
      //   final fieldList = classFieldsMap.putIfAbsent(
      //       (node.parent as dart_ast.ClassDeclaration).name.name,
      //       () => <String>[]);
      //   fieldList.add(fieldName);
      // }
      // fieldsTypeMap[fieldName] = type.name.name;

      Type t = Type()..type = type.name.name;
      field.type = t;

      clazz.fields.add(field);
    }

    return null;
  }

  @override
  Object? visitConstructorDeclaration(ConstructorDeclaration node) {
    stdout.writeln(
        'root visitConstructorDeclaration: node.name: ${node.name}, type: ${node.runtimeType} , ${node.initializers}, ${node.parent}');

    final clazz = _getClazz(node);
    if (clazz == null) return null;

    Constructor constructor = Constructor()
      ..name = node.name?.name ?? ''
      ..parameters = _getParameter(node.parent, node.parameters)
      ..isFactory = node.factoryKeyword != null
      ..comment = _generateComment(node);

    clazz.constructors.add(constructor);

    return null;
  }

  @override
  Object? visitEnumDeclaration(EnumDeclaration node) {
    stdout.writeln(
        'root visitEnumDeclaration: node.name: ${node.name}, type: ${node.runtimeType} constants: ${node.constants}, ${node.metadata}');
    for (final c in node.constants) {
      for (final m in c.metadata) {
        // stdout.writeln('m: ${m.arguments?.arguments}, ${m.name}');

        for (final a in m.arguments?.arguments ?? []) {
          stdout.writeln('a ${a.runtimeType}, ${a.toSource()}');
        }
      }
    }

    final enumz = enumMap.putIfAbsent(node.name.name, () => Enumz());
    enumz.name = node.name.name;
    enumz.comment = _generateComment(node);

    for (final constant in node.constants) {
      EnumConstant enumConstant = EnumConstant()
        ..name = '${node.name.name}.${constant.name.name}'
        ..comment = _generateComment(constant);
      enumz.enumConstants.add(enumConstant);

      for (final meta in constant.metadata) {
        SimpleAnnotation simpleAnnotation = SimpleAnnotation()
          ..name = meta.name.name;
        enumConstant.annotations.add(simpleAnnotation);

        for (final a in meta.arguments?.arguments ?? []) {
          SimpleLiteral simpleLiteral = SimpleLiteral();
          simpleAnnotation.arguments.add(simpleLiteral);

          late String type;
          late String value;

          if (a is IntegerLiteral) {
            type = 'int';
            value = a.value.toString();
          } else if (a is PrefixExpression) {
            if (a.operand is IntegerLiteral) {
              final operand = a.operand as IntegerLiteral;
              type = 'int';
              value = '${a.operator.value()}${operand.value.toString()}';
            }
          } else if (a is BinaryExpression) {
            type = 'int';
            value = a.toSource();
          }
          simpleLiteral.type = type;
          simpleLiteral.value = value;
        }
      }
    }

    return null;
  }

  Clazz? _getClazz(AstNode node) {
    final classNode = node.parent;
    if (classNode == null || classNode is! dart_ast.ClassDeclaration) {
      return null;
    }

    Clazz clazz = classMap.putIfAbsent(
      classNode.name.name,
      () => Clazz()
        ..name = classNode.name.name
        ..comment = _generateComment(node as AnnotatedNode),
    );

    return clazz;
  }

  List<Parameter> _getParameter(
      AstNode? root, FormalParameterList? formalParameterList) {
    if (formalParameterList == null) return [];
    List<Parameter> parameters = [];
    for (final p in formalParameterList.parameters) {
      Parameter parameter = Parameter();

      if (p is SimpleFormalParameter) {
        parameter.name = p.identifier?.name ?? '';
        DartType? dartType = p.type?.type;

        parameter.dartType = dartType;

        final namedType = p.type as NamedType;
        for (final ta in namedType.typeArguments?.arguments ?? []) {
          parameter.typeArguments.add(ta.name.name);
        }

        parameter.type = namedType.name.name;
        parameter.isNamed = p.isNamed;
        parameter.isOptional = p.isOptional;
      } else if (p is DefaultFormalParameter) {
        parameter.name = p.identifier?.name ?? '';
        parameter.defaultValue = p.defaultValue?.toSource();

        DartType? dartType;
        String? type;
        List<String> typeArguments = [];

        if (p.parameter is SimpleFormalParameter) {
          final SimpleFormalParameter simpleFormalParameter =
              p.parameter as SimpleFormalParameter;
          dartType = simpleFormalParameter.type?.type;

          final namedType = simpleFormalParameter.type as NamedType;
          for (final ta in namedType.typeArguments?.arguments ?? []) {
            typeArguments.add(ta.name.name);
          }

          type = (simpleFormalParameter.type as NamedType).name.name;
        } else if (p.parameter is FieldFormalParameter) {
          final FieldFormalParameter fieldFormalParameter =
              p.parameter as FieldFormalParameter;

          dartType = fieldFormalParameter.type?.type;

          if (root != null && root is ClassDeclaration) {
            for (final classMember in root.members) {
              if (classMember is FieldDeclaration) {
                final dart_ast.TypeAnnotation? fieldType =
                    classMember.fields.type;
                if (fieldType is dart_ast.NamedType) {
                  final fieldName = classMember.fields.variables[0].name.name;
                  if (fieldName == fieldFormalParameter.identifier.name) {
                    type = fieldType.name.name;
                    for (final ta in fieldType.typeArguments?.arguments ?? []) {
                      typeArguments.add(ta.name.name);
                    }
                    break;
                  }
                }
              }
            }
          }
        }
        parameter.dartType = dartType;
        parameter.type = type!;
        parameter.typeArguments.addAll(typeArguments);
        parameter.isNamed = p.isNamed;
        parameter.isOptional = p.isOptional;
      } else if (p is FieldFormalParameter) {
        String type = '';
        List<String> typeArguments = [];
        if (root != null && root is ClassDeclaration) {
          for (final classMember in root.members) {
            if (classMember is FieldDeclaration) {
              final dart_ast.TypeAnnotation? fieldType =
                  classMember.fields.type;
              if (fieldType is dart_ast.NamedType) {
                final fieldName = classMember.fields.variables[0].name.name;
                if (fieldName == p.identifier.name) {
                  type = fieldType.name.name;
                  for (final ta in fieldType.typeArguments?.arguments ?? []) {
                    typeArguments.add(ta.name.name);
                  }
                  break;
                }
              }
            }
          }
        }

        parameter.name = p.identifier.name;
        parameter.dartType = p.type?.type;
        parameter.type = type;
        parameter.typeArguments.addAll(typeArguments);
        parameter.isNamed = p.isNamed;
        parameter.isOptional = p.isOptional;
      }

      parameters.add(parameter);
    }

    return parameters;
  }

  CallApiInvoke? _getCallApiInvoke(Expression expression) {
    if (expression is! MethodInvocation) return null;

    if (expression.target != null) {
      return _getCallApiInvoke(expression.target!);
    }

    CallApiInvoke callApiInvoke = CallApiInvoke();
    for (final argument in expression.argumentList.arguments) {
      if (argument is SimpleStringLiteral) {
      } else if (argument is FunctionExpression) {
      } else if (argument is SetOrMapLiteral) {
        for (final element in argument.elements) {
          if (element is MapLiteralEntry) {
            final key = (element.key as SimpleStringLiteral).value;
            if (key == 'apiType') {
              callApiInvoke.apiType = element.value.toSource();
            } else if (key == 'params') {
              callApiInvoke.params = element.value.toSource();
            }
          }
        }
      }
    }

    return callApiInvoke;
  }

  SimpleComment _generateComment(AnnotatedNode node) {
    SimpleComment simpleComment = SimpleComment()
      ..offset = node.documentationComment?.offset ?? 0
      ..end = node.documentationComment?.end ?? 0;

    for (final token in node.documentationComment?.tokens ?? []) {
      simpleComment.commentLines.add(token.stringValue ?? '');
    }
    return simpleComment;
  }

  @override
  Object? visitMethodDeclaration(MethodDeclaration node) {
    final classNode = node.parent;
    if (classNode == null || classNode is! dart_ast.ClassDeclaration) {
      return null;
    }

    Clazz clazz = classMap.putIfAbsent(
      classNode.name.name,
      () => Clazz()..name = classNode.name.name,
    );

    Method method = Method()..name = node.name.name;
    clazz.methods.add(method);

    method.comment = _generateComment(node);

    if (node.parameters != null) {
      method.parameters.addAll(_getParameter(node.parent, node.parameters));
    }

    if (node.returnType != null && node.returnType is NamedType) {
      final returnType = node.returnType as NamedType;
      method.returnType = Type()
        ..type = returnType.name.name
        ..typeArguments = returnType.typeArguments?.arguments
                .map((ta) => (ta as NamedType).name.name)
                .toList() ??
            [];
    }

    if (node.body is BlockFunctionBody) {
      final body = node.body as BlockFunctionBody;

      FunctionBody fb = FunctionBody();
      method.body = fb;
      CallApiInvoke callApiInvoke = CallApiInvoke();
      method.body.callApiInvoke = callApiInvoke;

      for (final statement in body.block.statements) {
        if (statement is ReturnStatement) {
          final returns = statement as ReturnStatement;

          if (returns.expression != null) {
            CallApiInvoke? callApiInvoke =
                _getCallApiInvoke(returns.expression!);
            if (callApiInvoke != null) {
              method.body.callApiInvoke = callApiInvoke;
            }
          }
        }
      }
    }
    return null;
  }

  @override
  Object? visitGenericTypeAlias(dart_ast.GenericTypeAlias node) {
    stdout.writeln(
        'root visitGenericTypeAlias: node.name: ${node.name}, node.functionType?.parameters: ${node.functionType?.parameters.parameters}');

    final parametersList = node.functionType?.parameters.parameters
            .map((e) {
              if (e is SimpleFormalParameter) {
                return '${e.type} ${e.identifier?.name}';
              }
              return '';
            })
            .where((e) => e.isNotEmpty)
            .toList() ??
        [];

    stdout.writeln(parametersList);

    genericTypeAliasParametersMap[node.name.name] = parametersList;

    return null;
  }
}

class CodeVistor {
  const CodeVistor({required this.includedPaths});

  final List<String> includedPaths;

  ParseResult visit() {
    final DefaultVisitor rootBuilder = DefaultVisitor();

    visitWith(visitor: rootBuilder);

    final parseResult = ParseResult()
      ..classMap = rootBuilder.classMap
      ..enumMap = rootBuilder.enumMap
      ..genericTypeAliasParametersMap =
          rootBuilder.genericTypeAliasParametersMap;

    return parseResult;
  }

  void visitWith({required dart_ast_visitor.RecursiveAstVisitor visitor}) {
    final AnalysisContextCollection collection = AnalysisContextCollection(
      includedPaths: includedPaths,
    );

    for (final AnalysisContext context in collection.contexts) {
      for (final String path in context.contextRoot.analyzedFiles()) {
        final AnalysisSession session = context.currentSession;
        final ParsedUnitResult result =
            session.getParsedUnit(path) as ParsedUnitResult;
        if (result.errors.isEmpty) {
          final dart_ast.CompilationUnit unit = result.unit;
          unit.accept(visitor);
        } else {
          for (final AnalysisError error in result.errors) {
            stderr.writeln(error.toString());
          }
        }
      }
    }
  }
}
