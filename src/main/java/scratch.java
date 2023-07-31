import java.lang.reflect.Type;

public class scratch {

    public static void main(String[] args) {
        ExampleEnum exampleEnum = ExampleEnum.val(-1);
        if(exampleEnum==null)
            System.out.println("it's null");
        switch (exampleEnum){
            case VALUE2:
                System.out.println("matched");
                break;
            default:
                System.out.println("Not matched");
        }
    }
}

 enum ExampleEnum {
    VALUE1(1),
    VALUE2(2),
    VALUE3(3);

    private int id;
    private static ExampleEnum[] vals = values();

    public static ExampleEnum val(int i){
        if(i<0)
            return null;
        return vals[i];
    }
    private ExampleEnum(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}