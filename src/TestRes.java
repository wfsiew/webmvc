import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class TestRes {

	public static void main(String[] args) {
		try {
			URL url = TestRes.class.getResource("file1.txt");
			String file = url.getPath();
			file = file.substring(1);
			file = file.replace('/', '\\');
			System.out.println(file);
			Path path = Paths.get(file);
			List<String> l = Files.readAllLines(path);
			
			l.forEach(x -> System.out.println(x));
		}
		
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
