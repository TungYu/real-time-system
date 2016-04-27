package hw_RM;

import java.io.IOException;

public class main {
	public static void main(String[] args) throws IOException {
		
		realtimeSystem RM = new realtimeSystem();
		
		// "測資檔名"，1-RM . 2-EDF
		RM.doRealtime("test2",1);
		
	}
}
