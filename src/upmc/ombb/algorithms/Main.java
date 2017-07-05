package upmc.ombb.algorithms;


import com.sun.javafx.application.PlatformImpl;

import javafx.stage.Stage;

@SuppressWarnings("restriction")
public class Main {

	public static void main(String[] args) {
		PlatformImpl.startup(() -> {
			try {
				Frame f = new Frame();
				f.start(new Stage());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

}
