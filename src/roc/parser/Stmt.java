package roc.parser;

import roc.lexer.Token;

import java.util.List;

public abstract class Stmt {

	public interface Visitor<R> {
		R visitBlockStmt(Block stmt);
		R visitExpressionStmt(Expression stmt);
		R visitFunctionStmt(Function stmt);
		R visitIfStmt(If stmt);
		R visitPrintStmt(Print stmt);
		R visitReturnStmt(Return stmt);
		R visitVarStmt(Var stmt);
		R visitWhileStmt(While stmt);
	}
	public static class Block extends Stmt {

		public List<Stmt> statements;

		public Block(List<Stmt> statements) {
			this.statements = statements;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitBlockStmt(this);
		}
	}

	public static class Expression extends Stmt {

		public Expr expression;

		public Expression(Expr expression) {
			this.expression = expression;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitExpressionStmt(this);
		}
	}

	public static class Function extends Stmt {

		public Token name;
		public List<Token> params;
		public List<Stmt> body;

		public Function(Token name, List<Token> params, List<Stmt> body) {
			this.name = name;
			this.params = params;
			this.body = body;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitFunctionStmt(this);
		}
	}

	public static class If extends Stmt {

		public Expr condition;
		public Stmt thenBranch;
		public Stmt elseBranch;

		public If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
			this.condition = condition;
			this.thenBranch = thenBranch;
			this.elseBranch = elseBranch;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitIfStmt(this);
		}
	}

	public static class Print extends Stmt {

		public Expr expression;

		public Print(Expr expression) {
			this.expression = expression;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitPrintStmt(this);
		}
	}

	public static class Return extends Stmt {

		public Token keyword;
		public Expr value;

		public Return(Token keyword, Expr value) {
			this.keyword = keyword;
			this.value = value;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitReturnStmt(this);
		}
	}

	public static class Var extends Stmt {

		public Token name;
		public Expr initializer;

		public Var(Token name, Expr initializer) {
			this.name = name;
			this.initializer = initializer;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitVarStmt(this);
		}
	}

	public static class While extends Stmt {

		public Expr condition;
		public Stmt body;

		public While(Expr condition, Stmt body) {
			this.condition = condition;
			this.body = body;
		}

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitWhileStmt(this);
		}
	}

	public abstract <R> R accept(Visitor<R> visitor);
}
