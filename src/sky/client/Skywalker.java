package sky.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Position;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.Label;
import com.gwtext.client.widgets.layout.FitLayout;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Skywalker implements EntryPoint {

	private final int WIDTH = 320;
	private final int HEIGHT = 480;

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
		mainWindow.setSize(WIDTH, HEIGHT);
		mainWindow.setBorder(true);
		mainWindow.setPosition(0, 0);

		// panel inside the main window
		final Panel mainWindowPannel = new Panel();
		mainWindowPannel.setBorder(true);

		// -- add stuff to the main window panel here --
		buildApplication(mainWindowPannel);

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
						shellPanel.hide();
						mainWindow.show();
						mainWindow.center();
						// updates the gui
						mainWindow.doLayout();
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

	private void buildApplication(final Panel mainWindowPannel) {
		// set up all dynamic content - buttons, panels, listeners, etc

		// main map area
		final Panel mapPanel = buildMapPanel();

		// main areas for toggle content
		final Panel directionPanel = buildDirectionPanel();
		final Panel locationPanel = buildLocationPanel();
		final Panel favoritePanel = buildFavoritePanel();

		// setup bottom toggles on main window
		Toolbar bottomBar = buildBottomToggleBar(mainWindowPannel, mapPanel,
				directionPanel, locationPanel, favoritePanel);
		mainWindowPannel.setBottomToolbar(bottomBar);

		// load map as default starting point
		clearAndLoad(mainWindowPannel, mapPanel);
	}

	/**
	 * Builds the main toggle buttons that belong on the bottom of the main
	 * screen. These toggles have listeners that clear the main window and show
	 * the given panels when clicked.
	 * 
	 * @param mainWindowPannel
	 * @param mapPanel
	 * @param directionPanel
	 * @param locationPanel
	 * @param favoritePanel
	 * @param bottomBar
	 */
	private Toolbar buildBottomToggleBar(final Panel mainWindowPannel,
			final Panel mapPanel, final Panel directionPanel,
			final Panel locationPanel, final Panel favoritePanel) {

		Toolbar bottomBar = new Toolbar();

		final ToolbarButton directionsToggle = buildToggle(bottomBar, "Directions");
		final ToolbarButton locationToggle = buildToggle(bottomBar, "Get Location");
		final ToolbarButton favoriteToggle = buildToggle(bottomBar, "Favorites");

		directionsToggle.addListener(new ButtonListenerAdapter() {
			public void onClick(Button button, EventObject e) {
				// button actions
				if (button.isPressed()) {
					// unpress other buttons
					locationToggle.setPressed(false);
					favoriteToggle.setPressed(false);
					// show direction panel
					clearAndLoad(mainWindowPannel, directionPanel);
				} else {
					// hide direction panel
					clearAndLoad(mainWindowPannel, mapPanel);
				}
			}
		});

		locationToggle.addListener(new ButtonListenerAdapter() {
			public void onClick(Button button, EventObject e) {
				// button actions
				if (button.isPressed()) {
					// unpress other buttons
					directionsToggle.setPressed(false);
					favoriteToggle.setPressed(false);
					// show location panel
					clearAndLoad(mainWindowPannel, locationPanel);
				} else {
					// hide location panel
					clearAndLoad(mainWindowPannel, mapPanel);
				}
			}
		});

		favoriteToggle.addListener(new ButtonListenerAdapter() {
			public void onClick(Button button, EventObject e) {
				// button actions
				if (button.isPressed()) {
					// unpress other buttons
					directionsToggle.setPressed(false);
					locationToggle.setPressed(false);
					// show favorite panel
					clearAndLoad(mainWindowPannel, favoritePanel);
				} else {
					// hide favorite panel
					clearAndLoad(mainWindowPannel, mapPanel);
				}
			}

		});

		return bottomBar;
	}

	private ToolbarButton buildToggle(Toolbar bottomBar, String title) {
		ToolbarButton toggle = new ToolbarButton(title);
		toggle.setEnableToggle(true);
		toggle.setPressed(false);
		// 6 pixels for padding
		toggle.setMinWidth((WIDTH / 3) - 6);
		bottomBar.addButton(toggle);
		return toggle;
	}

	private Panel buildFavoritePanel() {
		// TODO: implement me!
		// TabPanel favoriteTabs = new TabPanel();
		// favoriteTabs.setTabPosition(Position.TOP);
		// favoriteTabs.setResizeTabs(false);
		// favoriteTabs.setActiveTab(0);
		//		
		// favoriteTabs.add(buildTestPanel("Location", "location tab content"));
		//		
		// Panel favoritePanel = new Panel();
		// favoritePanel.setLayout(new FitLayout());
		// favoritePanel.add(favoriteTabs);

		return buildTestPanel("Favarite panel go here");
	}

	private Panel buildLocationPanel() {
		// TODO: implement me!
		return buildTestPanel("Location panel goes here");
	}

	private Panel buildDirectionPanel() {
		// TODO: implement me!
		return buildTestPanel("Directions panel goes here");
	}

	private Panel buildMapPanel() {
		// TODO: implement me!
		return buildTestPanel("Map panel goes here");
	}

	private Panel buildTestPanel(String testMessage) {
		Panel testPanel = new Panel();
		testPanel.setLayout(new FitLayout());
		Label test = new Label(testMessage);
		testPanel.add(test);
		return testPanel;
	}

	private void clearAndLoad(Panel parentToClear, Panel childToAdd) {
		parentToClear.clear();
		childToAdd.show();
		parentToClear.add(childToAdd);
		// updates the gui
		parentToClear.doLayout();
	}

}
