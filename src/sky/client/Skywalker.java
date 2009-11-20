package sky.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.layout.FitLayout;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Skywalker implements EntryPoint {
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		// create phone window
		final Window mainWindow = new Window();
		mainWindow.setTitle("Skywalker");
		mainWindow.setMaximizable(false);
		mainWindow.setResizable(false);
		mainWindow.setModal(false);
		mainWindow.setLayout(new FitLayout());
		mainWindow.setSize(320, 480);
		mainWindow.setBorder(true);
		mainWindow.setPosition(0, 0);
		
		// panel inside the main window
		final Panel mainWindowPannel = new Panel();
		mainWindowPannel.setBorder(true);
		
		// -- add stuff to the main window panel here --
		buildApp(mainWindowPannel);
		
		// connect main window panel to the main window
		mainWindow.add(mainWindowPannel);
		
		// outermost panel that holds the launch button
		final Panel shellPanel = new Panel();
		shellPanel.setBorder(true);
		shellPanel.setTitle("Skywalker Demo");
		shellPanel.setWidth(120);

		final Button launchButton = new Button("Launch Demo!",
				new ButtonListenerAdapter() {
			public void onClick(Button button, EventObject e) {
				mainWindow.show();
				mainWindow.center();
			}
		});
		// We can add style names to widgets -- styled with css
		launchButton.addStyleName("launchButton");
		launchButton.setStyle("align: center;");
		
		shellPanel.add(launchButton);
		
		// Add the launchButton to the RootPanel
		// Use RootPanel.get() to get the entire body element
		RootPanel.get().add(shellPanel);
	}
	
	private void buildApp(Panel mainWindowPannel) {
		// set up all dynamic content - buttons, panels, listeners, etc
	}
}
