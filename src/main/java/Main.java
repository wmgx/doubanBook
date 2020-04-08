import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        InputStream in = Main.class.getClassLoader().getResourceAsStream("src.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String s;
        while ((s = reader.readLine()) != null) {
            // ����cookie
            // System.out.println(ParseDouban.getInfo(s));
            // ����cookie �Ƽ�
            System.out.println(ParseDouban.getInfo(s,"Yourcookie"));
        }
    }
}
