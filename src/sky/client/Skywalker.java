package sky.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.Label;
import com.gwtext.client.widgets.layout.ColumnLayout;
import com.gwtext.client.widgets.layout.ColumnLayoutData;
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtext.client.widgets.layout.VerticalLayout;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Skywalker implements EntryPoint {

	private final int WIDTH = 320;
	private final int HEIGHT = 480;
	private final String FAV_LINK_TEXTSTYLE = "font-family: Verdana;color: blue;font-size: 20px;text-align: center;text-decoration: underline;";

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
		shellPanel.setBorder(false);
		shellPanel.setTitle("Skywalker Demo");
		shellPanel.setWidth(105);

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
		launchButton.setMinWidth(105);

		shellPanel.add(launchButton);

		// Add the launchButton to the RootPanel
		// Use RootPanel.get() to get the entire body element
		RootPanel.get().add(shellPanel);
	}

	private void buildApplication(final Panel mainWindowPannel) {
		// set up all dynamic content - buttons, panels, listeners, etc

		// main map area
		Panel mapPanel = buildMapPanel();

		// main areas for toggle content
		Panel directionPanel = buildDirectionPanel();
		Panel locationPanel = buildLocationPanel();
		Panel favoritePanel = buildFavoritePanel();

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

		final ToolbarButton directionsToggle = buildToggle(bottomBar,
				"Directions");
		final ToolbarButton locationToggle = buildToggle(bottomBar,
				"Get Location");
		final ToolbarButton favoriteToggle = buildToggle(bottomBar, "Favorites");

		directionsToggle.addListener(new ButtonListenerAdapter() {
			public void onClick(Button button, EventObject e) {
				// button actions
				if (button.isPressed()) {
					// unpress other buttons
					locationToggle.setPressed(false);
					favoriteToggle.setPressed(false);
					// rebuild and show direction panel
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
		// wrapper for the tabs component
		Panel favoritePanel = new Panel("Favorites");
		favoritePanel.setLayout(new FitLayout());

		// the tabs
		TabPanel tabs = new TabPanel();
		tabs.setResizeTabs(false);
		tabs.setBorder(false);
		// apparently you cant set the layout of a tabbed panel
		tabs.setHeight(HEIGHT);

		tabs.add(buildWalkTab());
		tabs.add(buildLocationTab());

		favoritePanel.add(tabs);

		return favoritePanel;
	}

	private Panel buildLocationTab() {
		Panel tab = new Panel("Locations");
		tab.setLayout(new ColumnLayout());
		tab.setBorder(false);

		// set up label column
		Panel labels = new Panel();
		// 30 pixels between components
		labels.setLayout(new VerticalLayout(28));
		labels.setBorder(false);

		// add labels to label column
		Label loc1 = new Label("(1)  Macy's");
		labels.add(loc1);
		loc1.setStyle(FAV_LINK_TEXTSTYLE);
		Label loc2 = new Label("(2)  Target Plaza");
		labels.add(loc2);
		loc2.setStyle(FAV_LINK_TEXTSTYLE);
		Label loc3 = new Label("(3)  Target Store");
		loc3.setStyle(FAV_LINK_TEXTSTYLE);
		labels.add(loc3);

		// set up image column
		Panel images = new Panel();
		// 30 pixels between components
		images.setLayout(new VerticalLayout(30));
		images.setBorder(false);

		ClickHandler ch = new ClickHandler() {

			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				System.out.println("Image clicked");
			}
		};

		// add images to image column
		Image deleteImage = new Image("images/delete.png");
		deleteImage.addClickHandler(ch);
		images.add(deleteImage);

		Image deleteImage2 = new Image("images/delete.png");
		deleteImage2.addClickHandler(ch);
		images.add(deleteImage2);

		Image deleteImage3 = new Image("images/delete.png");
		deleteImage3.addClickHandler(ch);
		images.add(deleteImage3);

		tab.add(labels, new ColumnLayoutData(.85));
		tab.add(images, new ColumnLayoutData(.15));

		return tab;
	}

	private Panel buildWalkTab() {
		Panel tab = new Panel("Walks");
		tab.setLayout(new ColumnLayout());
		tab.setBorder(false);

		// set up label column
		Panel labels = new Panel();
		// 30 pixels between components
		labels.setLayout(new VerticalLayout(30));
		labels.setBorder(false);

		// add labels to label column
		Label loc1 = new Label("(1)  Macy's to Target Plaza");
		loc1.setStyle(FAV_LINK_TEXTSTYLE);
		labels.add(loc1);
		Label loc2 = new Label("(2)  Target Plaza to Target Store");
		loc2.setStyle(FAV_LINK_TEXTSTYLE);
		labels.add(loc2);

		// set up image column
		Panel images = new Panel();
		// 30 pixels between components
		images.setLayout(new VerticalLayout(57));
		images.setBorder(false);

		ClickHandler ch = new ClickHandler() {

			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				System.out.println("Image clicked");
			}
		};

		// add images to image column
		Image deleteImage = new Image("images/delete.png");
		deleteImage.addClickHandler(ch);
		images.add(deleteImage);

		Image deleteImage2 = new Image("images/delete.png");
		deleteImage2.addClickHandler(ch);
		images.add(deleteImage2);

		tab.add(labels, new ColumnLayoutData(.85));
		tab.add(images, new ColumnLayoutData(.15));

		return tab;
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
		return buildTestPanel(testMessage, "TestTitle");
	}

	private Panel buildTestPanel(String testMessage, String title) {
		Panel testPanel = new Panel(title);
		testPanel.setLayout(new FitLayout());
		Label test = new Label(testMessage);
		testPanel.add(test);
		return testPanel;
	}

	private void clearAndLoad(Panel parentToClear, Panel childToAdd) {
		parentToClear.removeAll();
		childToAdd.show();
		parentToClear.add(childToAdd);
		// updates the gui
		parentToClear.doLayout();
	}

}
