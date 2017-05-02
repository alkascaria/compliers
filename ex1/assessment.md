# Lexer

Correct extension.

Points: 1/1

# Parser

## Positive

- Correct extension of the parser
- Disambiguation using precedence rules
- Good explanation in readme (event though)

## Negative

- Grammar much too complex
  - Negation should not be handled by all different rules but only by one (- expr:e)
  - Handling of paranthesis in all rules leads to more conflicts than necessary (first rule covers all cases already)
  - What is `ExprBracket` for?

Points: 2/3

# Visitor

## Positive

The general idea of accept method in the AST classes and dispatch to the visitor seems to be clear. Evaluation be calling the `accept` method is also ok.

## Negative

- Different accept methods for different algorithms
  - The visitor pattern should be generic, but in your implementation, the `accept` returns `String` and `acceptEvaluate` return `int`. Even the documentation talk about `accept` printing the expression. Too complex and specific!!
- Special case for single integers in `Main.prettyPrint` (that should all be done in the `ExprPrinter` implementation)

Points: 1/3

# Evaluator

Why is this part of `ExprPrinter`?

## Positive

- Evaluation ok
- Tree traversal using the `acceptEvaluate` methods

## Negative

- Not really according to the visitor pattern: New algorithm => new subclass of `ExprVisitor`

Points: 2/3
