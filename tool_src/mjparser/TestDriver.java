import java_cup.runtime.*;
import java.io.FileInputStream;
public class TestDriver {

	public static void main(String[] args) throws Exception{
		new parser(new Yylex(new FileInputStream(args[0]))).parse();		
	}

}
