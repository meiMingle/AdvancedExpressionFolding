package data;

public class InterpolatedStringTestData {
    public static void main(String[] args) <fold text='{...}' expand='true'>{
        System.out.println("Hello, <fold text='${' expand='false'>" + </fold>args[0]<fold text='}")' expand='false'>)</fold>;
        System.out.println("Hello, <fold text='${' expand='false'>" + </fold>args[0]<fold text='}' expand='false'> + "</fold>!");
        System.out.println<fold text='("${' expand='false'>(</fold>args[0]<fold text='}' expand='false'> + "</fold>, hello!");
        System.out.println<fold text='("${' expand='false'>(</fold>args[0]<fold text='}' expand='false'> + "</fold>, <fold text='${' expand='false'>" + </fold>args[0]<fold text='}")' expand='false'>)</fold>;
        <fold text='val' expand='false'>String</fold> name = args[0];
        System.out.println("Hello, <fold text='$' expand='false'>" + </fold>name<fold text='")' expand='false'>)</fold>;
        System.out.println("Hello, <fold text='$' expand='false'>" + </fold>name<fold text='' expand='false'> + "</fold>!");
        System.out.println<fold text='("$' expand='false'>(</fold>name<fold text='' expand='false'> + "</fold>, hello!");
        System.out.println<fold text='("$' expand='false'>(</fold>name<fold text='' expand='false'> + "</fold>, <fold text='$' expand='false'>" + </fold>name<fold text='")' expand='false'>)</fold>;
    }</fold>
}
