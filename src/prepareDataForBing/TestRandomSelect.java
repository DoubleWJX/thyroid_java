package prepareDataForBing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestRandomSelect {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		List<String> list = new ArrayList<>();
		for(int i = 0; i < 100; ++i){
			list.add("" + i + ".jpg");
		}
		Collections.shuffle(list);
		
		for(int i = 0; i < 100; ++i){
			System.out.println(list.get(i));
		}
		
		System.out.println((int) 0.1);
	}

}
