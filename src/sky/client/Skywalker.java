package sky.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.SimpleStore;
import com.gwtext.client.data.Store;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.QuickTipsConfig;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.ComboBox;
import com.gwtext.client.widgets.form.Field;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.form.event.FieldListenerAdapter;
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtext.client.widgets.layout.HorizontalLayout;
import com.gwtext.client.widgets.layout.VerticalLayout;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Skywalker implements EntryPoint {

	// ipod - 320W x 480H
	private final int WIDTH = 300;
	private final int HEIGHT = 460;
	private final String FAV_LINK_TEXTSTYLE = "font-family: Verdana;color: blue;font-size: 20px;text-align: center;text-decoration: underline;";
	private final String[][] DIRECTION_DATA = new String[][] {
			new String[] { "[Current Location]" },
			new String[] { "Hilton" },
			new String[] { "Macy's" }, 
			new String[] { "Midwest Plaza" },
			new String[] { "Parking - Target Store" },
			new String[] { "Parking - Midwest Plaza" },
			new String[] { "Parking - Hilton" },
			new String[] { "Target Center" },
			new String[] { "Target Plaza" }, 
			new String[] { "Target Store" },
			};
	private final String[][] PACE_DATA = new String[][] {
			new String[] { "Slow" }, new String[] { "Medium" },
			new String[] { "Fast" } };

	// maps stuff
	private final int CENTER = 0;
	private final int UP = 1;
	private final int LEFT = 2;
	private final int RIGHT = 3;
	private final int DOWN = 4;
	private final int NO_DIRECTION = 0;
	private final int TARGET_MACY = 1;
	private final int TARGET_TARGET = 2;
	private final int TARGET_MACY_30 = 3;
	private int currentLocation = CENTER;
	private int currentDirection = NO_DIRECTION;

	// various widgets - can be used during updates
	final Panel mapMainPanel = new Panel();
	final Panel mainPannel = new Panel("Skywalker Demo");
	final Panel directionPanel = new Panel("Directions");
	final Panel mapPanel = new Panel("Map");
	final Panel favoritePanel = new Panel("Favorites");
	final ScrollPanel mapScroll = new ScrollPanel();
	final ComboBox fromCombo = new ComboBox();
	final ComboBox toCombo = new ComboBox();
	final TextBox spinner = new TextBox();
	final ToolbarButton zoomButton = new ToolbarButton();
	final Panel mapScrollingTab = new Panel("View Map");
	final Panel directionsList = new Panel("View Directions");
	final ToggleButton save = new ToggleButton("Save");
	final ToggleButton go = new ToggleButton("Go!");

	final String html = "<p>From: Target Plaza<br>To: Macy's<br>Distance: 0.5 miles<br>Pace: Medium<br>Estimated Time: 30 minutes<br><br>1. Exit target 2nd floor main doors<br>2. Head to the right<br>3. Follow signs for the 'Highland Bank' building<br>4. ...</p>";
	final String nohtml = "<p>Enter locations to receive directions...</p>";

	private boolean zoomedIn = false;
	private boolean showLocation = true;
	private boolean showDirection = false;
	private boolean showLong = false;
	private boolean isMinutes = false;
	private boolean saveButtonsReady = false;

	Image currentImage;

	final Image inNormal = new Image("images/sky_map-in.png");
	final Image inLoc = new Image("images/sky_map-in-location.png");
	final Image inDirShort = new Image("images/sky_map-in-direction_short.png");
	final Image inDirLong = new Image("images/sky_map-in-direction_long.png");
	final Image inLocDirShort = new Image(
			"images/sky_map-in-location_direction_short.png");
	final Image inLocDirLong = new Image(
			"images/sky_map-in-location_direction_long.png");

	final Image outNormal = new Image("images/sky_map-out.png");
	final Image outLoc = new Image("images/sky_map-out-location.png");
	final Image outDirShort = new Image(
			"images/sky_map-out-direction_short.png");
	final Image outDirLong = new Image("images/sky_map-out-direction_long.png");
	final Image outLocDirShort = new Image(
			"images/sky_map-out-location_direction_short.png");
	final Image outLocDirLong = new Image(
			"images/sky_map-out-location_direction_long.png");

	final Toolbar bottomBar = new Toolbar();
	final ToolbarButton directionsToggle = new ToolbarButton("Directions");
	final ToolbarButton locationToggle = new ToolbarButton("Get Location");
	final ToolbarButton favoriteToggle = new ToolbarButton("Favorites");

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		// panel inside the main window
		mainPannel.setBorder(false);
		mainPannel.setLayout(new FitLayout());

		buildApplication();

		// load default starting point
		mainPannel.add(directionPanel);

		RootPanel.get().add(mainPannel);

		// updates the gui
		mainPannel.setSize(WIDTH, HEIGHT);
		mainPannel.doLayout();
	}

	private void buildApplication() {
		buildMapPanel();
		buildDirectionPanel();
		buildFavoritePanel();

		// setup bottom toggles on main window
		mainPannel.setBottomToolbar(buildBottomToggleBar());
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
	private Toolbar buildBottomToggleBar() {

		bottomBar.setWidth(WIDTH);

		buildToggle(directionsToggle);
		buildToggle(locationToggle);
		buildToggle(favoriteToggle);

		// directions is default
		directionsToggle.setPressed(true);

		directionsToggle.addListener(new ButtonListenerAdapter() {
			public void onClick(Button button, EventObject e) {
				// button actions
				if (button.isPressed()) {
					// unpress other buttons
					locationToggle.setPressed(false);
					favoriteToggle.setPressed(false);
					// rebuild and show direction panel
					updateSaveGoButtons();
					showPanel(directionPanel);
				} else {
					// hide direction panel
					updateMap();
					showPanel(mapPanel);
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
					button.setPressed(false);
					// show location panel
					showLocation = true;
					updateMap();
					showPanel(mapPanel);
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
					showPanel(favoritePanel);
				} else {
					// hide favorite panel
					updateMap();
					showPanel(mapPanel);
				}
			}

		});

		return bottomBar;
	}

	private void buildToggle(ToolbarButton toggle) {
		toggle.setEnableToggle(true);
		toggle.setPressed(false);
		toggle.setMinWidth((WIDTH / 3));
		bottomBar.addButton(toggle);
	}

	private void buildFavoritePanel() {
		TabPanel tabs = new TabPanel();
		tabs.setResizeTabs(false);
		tabs.setBorder(false);

		tabs.add(buildWalkTab());
		tabs.add(buildLocationTab());

		favoritePanel.add(tabs);
		favoritePanel.setBorder(false);
	}

	private Panel buildLocationTab() {
		Panel tab = new Panel("Locations");
		tab.setLayout(new VerticalLayout(15));
		tab.setBorder(false);

		tab.add(buildFavoriteEntry("1. Hilton", false));
		tab.add(buildFavoriteEntry("2. Macy's", false));
		tab.add(buildFavoriteEntry("3. Target Plaza", false));
		tab.add(buildFavoriteEntry("4. Target Store", false));

		return tab;
	}

	private Panel buildWalkTab() {
		Panel tab = new Panel("Walks");
		tab.setLayout(new VerticalLayout(15));
		tab.setBorder(false);

		tab.add(buildFavoriteEntry("1. Target Plaza to Macy's", true));
		tab.add(buildFavoriteEntry("2. Hilton to Target Plaza", true));

		return tab;
	}

	private Panel buildFavoriteEntry(final String entryTitle,
			final boolean isWalk) {
		// an entry in the favorite list has a button with the entry title
		final ToggleButton button = new ToggleButton(entryTitle);
		// delete image is 25 pixels high too!
		button.setPixelSize(225, 25);
		button.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				System.out.println("Toggle clicked: " + entryTitle);
				button.setDown(false);
				fromCombo.setValue("Target Plaza");
				if (isWalk) {
					toCombo.setValue("Macy's");
				}
				favoriteToggle.setPressed(false);
				directionsToggle.setPressed(true);
				updateSaveGoButtons();
				showPanel(directionPanel);
			}
		});

		// the entry also has an clickable image to delete the entry
		final ToggleButton deleteImage = new ToggleButton(new Image(
				"images/delete.png"));
		deleteImage.setPixelSize(25, 25);
		deleteImage.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				System.out.println("Delete image clicked");
				deleteImage.setDown(false);
			}
		});

		// create the new panel and add componenets
		Panel entry = new Panel();
		entry.setBorder(false);
		entry.setHeader(false);
		entry.setLayout(new HorizontalLayout(10));
		entry.setMargins(15, 6, 0, 0);

		entry.add(button);
		entry.add(deleteImage);

		return entry;
	}

	private void buildDirectionPanel() {
		directionPanel.setLayout(new VerticalLayout(25));
		directionPanel.setMargins(10, 10, 10, 10);
		directionPanel.setBorder(false);

		directionPanel.add(buildSearchBox(fromCombo, "From:"));
		directionPanel.add(buildSearchBox(toCombo, "To:"));
		if (showLocation) {
			fromCombo.setValue("[Current Location]");
		}

		Panel constraints = new Panel("Options:", 275, 100);
		constraints.setLayout(new VerticalLayout(10));
		constraints.setBorder(false);
		constraints.setHeader(true);

		constraints.add(buildPace());
		constraints.add(buildRadioOpts());

		directionPanel.add(constraints);
		directionPanel.add(buildDirectionsButtons());
		
		saveButtonsReady = true;
		updateSaveGoButtons();
	}

	private Panel buildSearchBox(ComboBox cb, String title) {
		final Store store = new SimpleStore(new String[] { "location" },
				DIRECTION_DATA);
		store.load();

		cb.setMinChars(1);
		// we have a custom label
		cb.setHideLabel(true);
		cb.setStore(store);
		cb.setDisplayField("location");
		cb.setMode(ComboBox.LOCAL);
//		cb.setTriggerAction(ComboBox.ALL);
		cb.setEmptyText("Enter location");
		cb.setLoadingText("Searching...");
		cb.setTypeAhead(true);
		cb.setSelectOnFocus(true);
		cb.setForceSelection(true);
		cb.setEditable(true);
		cb.setAllowBlank(false);
		cb.setPageSize(10);

		cb.setWidth(230);
		
		cb.addListener(new FieldListenerAdapter() {

			@Override
			public void onBlur(Field field) {
				// TODO Auto-generated method stub
				super.onBlur(field);
				updateSaveGoButtons();
			}

			@Override
			public void onFocus(Field field) {
				// TODO Auto-generated method stub
				super.onFocus(field);
				updateSaveGoButtons();
			}
		});

		FormPanel form = new FormPanel();
		// form.setMargins(0, 0, 25, 0);
		form.setWidth(270);
		form.setBorder(false);
		form.add(cb);

		Panel wrapper = new Panel(title);
		wrapper.setBorder(false);
		wrapper.setWidth(275);
		wrapper.setLayout(new FitLayout());
		wrapper.add(form);

		return wrapper;
	}

	private Panel buildPace() {
		final Store store = new SimpleStore(new String[] { "pace" }, PACE_DATA);
		store.load();

		Label pace = new Label("Pace:");
		pace.setPixelSize(40, 25);

		ComboBox cb = new ComboBox();
		cb.setHideLabel(true);
		cb.setStore(store);
		cb.setDisplayField("pace");
		cb.setMode(ComboBox.LOCAL);
		cb.setTriggerAction(ComboBox.ALL);
		cb.setEmptyText("Enter desired pace");
		cb.setLoadingText("Searching...");
		cb.setTypeAhead(true);
		cb.setSelectOnFocus(true);
		cb.setForceSelection(true);
		cb.setEditable(false);
		cb.setAllowBlank(false);
		cb.setWidth(100);
		cb.setPageSize(0);
		cb.setValue("Medium");

		FormPanel form = new FormPanel();
		form.setLabelWidth(40);
		form.setBorder(false);
		form.add(cb);
		form.setWidth(200);
		form.setAutoScroll(false);

		Panel p = buildRowPanel();
		p.add(pace);
		p.add(form);

		return p;
	}

	private Panel buildRowPanel() throws IllegalStateException {
		Panel p = new Panel("", WIDTH - 30, 25);
		p.setBorder(false);
		p.setHeader(false);
		p.setLayout(new HorizontalLayout(10));
		// p.setAutoWidth(true); TODO
		return p;
	}

	private Panel buildRadioOpts() {
		RadioButton miles = new RadioButton("myRadioButton", "Miles");
		miles.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				System.out.println("miles clicked");
				spinner.setValue("0.5");
				isMinutes = false;
			}
		});
		RadioButton minutes = new RadioButton("myRadioButton", "Minutes");
		minutes.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				System.out.println("minutes clicked");
				spinner.setValue("30");
				isMinutes = true;
			}
		});

		spinner.setValue("30");
		spinner.setVisibleLength(5);
		minutes.setValue(true, true);
		isMinutes = true;

		Panel milesWrapper = new Panel();
		milesWrapper.setLayout(new FitLayout());
		milesWrapper.setPixelSize(75, 25);
		milesWrapper.setBorder(false);
		milesWrapper.add(miles);

		Panel minutesWrapper = new Panel();
		minutesWrapper.setLayout(new FitLayout());
		minutesWrapper.setPixelSize(75, 25);
		minutesWrapper.setBorder(false);
		minutesWrapper.add(minutes);

		Panel panel = buildRowPanel();
		panel.add(milesWrapper);
		panel.add(minutesWrapper);
		panel.add(spinner);

		return panel;
	}

	private Panel buildDirectionsButtons() {
		save.setPixelSize(50, 25);
		save.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				// TODO to be implemented (if needed)
				System.out.println("Save button clicked");
				save.setDown(false);
			}
		});
		go.setPixelSize(50, 25);
		go.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				System.out.println("Go button clicked");
				showDirection = true;
				go.setDown(false);

				if (isMinutes && Double.parseDouble(spinner.getValue()) > 30.0) {
					showLong = true;
				} else if (!isMinutes
						&& Double.parseDouble(spinner.getValue()) > 0.5) {
					showLong = true;
				} else {
					showLong = false;
				}

				showPanel(mapPanel);
				directionsToggle.setPressed(false);
				updateMap();
			}
		});
		Panel panel = new Panel();
		panel.setLayout(new HorizontalLayout(10));
		panel.setBorder(false);
		panel.setHeader(false);
		panel.add(save);
		panel.add(go);
		panel.setMargins(0, 60, 0, 0);

		return panel;
	}

	private Panel buildMapPanelOld() {
		Panel topPanel = buildTopPanel();
		Panel mapPanel = buildMap();
		mapMainPanel.add(topPanel);
		mapMainPanel.add(mapPanel);
		return mapMainPanel;
	}

	private void updateMap() {
		mapScroll.remove(currentImage);
		currentImage = getMapImage();
		mapScroll.add(currentImage);

		setScrolls();

		if (showDirection) {
			directionsList.setDisabled(false);
			directionsList.setHtml(html);
		} else {
			directionsList.setDisabled(true);
			directionsList.setHtml(nohtml);
		}

		mapPanel.doLayout();
	}

	private Panel buildTopPanel() {
		Panel topPanel = new Panel();
		topPanel.setLayout(new HorizontalLayout(50));

		// create panning images button
		Panel panPanel = new Panel();

		final ToggleButton upImage = new ToggleButton(
				new Image("images/up.jpg"));
		upImage.setPixelSize(10, 10);
		upImage.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (zoomedIn && !showLocation && currentLocation == CENTER) {
					currentLocation = UP;
					updateMap();
				} else if (zoomedIn && !showLocation && currentLocation == DOWN) {
					currentLocation = CENTER;
					updateMap();
				}
				System.out.println("Up clicked");
				upImage.setDown(false);
			}
		});
		Panel upPanPanel = new Panel();
		upPanPanel.setPaddings(0, 15, 0, 0);
		upPanPanel.add(upImage);

		Panel leftRightPanPanel = new Panel();
		leftRightPanPanel.setLayout(new HorizontalLayout(15));
		final ToggleButton leftImage = new ToggleButton(new Image(
				"images/left.jpg"));
		leftImage.setPixelSize(10, 10);
		leftImage.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (zoomedIn && !showLocation && currentLocation == RIGHT) {
					currentLocation = CENTER;
					updateMap();
				} else if (zoomedIn && !showLocation
						&& currentLocation == CENTER) {
					currentLocation = LEFT;
					updateMap();
				}
				System.out.println("Left clicked");
				leftImage.setDown(false);
			}
		});
		final ToggleButton rightImage = new ToggleButton(new Image(
				"images/right.jpg"));
		rightImage.setPixelSize(10, 10);
		rightImage.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (zoomedIn && !showLocation && currentLocation == LEFT) {
					currentLocation = CENTER;
					updateMap();
				} else if (zoomedIn && !showLocation
						&& currentLocation == CENTER) {
					currentLocation = RIGHT;
					updateMap();
				}
				System.out.println("Right clicked");
				rightImage.setDown(false);
			}
		});
		leftRightPanPanel.add(leftImage);
		leftRightPanPanel.add(rightImage);

		final ToggleButton downImage = new ToggleButton(new Image(
				"images/down.jpg"));
		downImage.setPixelSize(10, 10);
		downImage.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (zoomedIn && !showLocation && currentLocation == UP) {
					currentLocation = CENTER;
					updateMap();
				} else if (zoomedIn && !showLocation
						&& currentLocation == CENTER) {
					currentLocation = DOWN;
					updateMap();
				}
				System.out.println("Down clicked");
				downImage.setDown(false);
			}
		});
		Panel downPanPanel = new Panel();
		downPanPanel.setPaddings(0, 15, 0, 0);
		downPanPanel.add(downImage);

		panPanel.add(upPanPanel);
		panPanel.add(leftRightPanPanel);
		panPanel.add(downPanPanel);
		topPanel.add(panPanel);

		// create zoom images button
		Panel zoomPanel = new Panel();
		if (!zoomedIn) {
			final ToggleButton zoomInImage = new ToggleButton(new Image(
					"images/zoomInButton.jpg"));
			zoomInImage.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					System.out.println("ZoomIn clicked");
					zoomedIn = true;
					zoomInImage.setDown(false);
					updateMap();
				}
			});
			final ToggleButton zoomOutImage = new ToggleButton(new Image(
					"images/zoomOut.jpg"));
			zoomOutImage.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					zoomOutImage.setDown(false);
				}
			});
			zoomPanel.add(zoomInImage);
			zoomPanel.add(zoomOutImage);
		} else {
			final ToggleButton zoomInImage = new ToggleButton(new Image(
					"images/zoomIn.jpg"));
			zoomInImage.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					zoomInImage.setDown(false);
				}
			});
			final ToggleButton zoomOutImage = new ToggleButton(new Image(
					"images/zoomOutButton.jpg"));
			zoomOutImage.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					System.out.println("ZoomOut clicked");
					zoomedIn = false;
					currentLocation = CENTER;
					zoomOutImage.setDown(false);
					updateMap();
				}
			});
			zoomPanel.add(zoomInImage);
			zoomPanel.add(zoomOutImage);
		}
		topPanel.add(zoomPanel);

		// create directions button if needed
		if (currentDirection != NO_DIRECTION) {
			final Panel directionPanel = new Panel();
			final ToggleButton directions = new ToggleButton("Info");
			directions.setPixelSize(50, 20);
			directions.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					System.out.println("Directions clicked");
					// TODO add popup
					// Create the new popup.
					final PopupPanel popup = new PopupPanel(true);
					// Position the popup 1/3rd of the way down and across the
					// screen, and
					// show the popup. Since the position calculation is based
					// on the
					// offsetWidth and offsetHeight of the popup, you have to
					// use the
					// setPopupPositionAndShow(callback) method. The alternative
					// would
					// be to call show(), calculate the left and top positions,
					// and
					// call setPopupPosition(left, top). This would have the
					// ugly side
					// effect of the popup jumping from its original position to
					// its
					// new position.
					/*
					 * popup.setPopupPositionAndShow(new
					 * PopupPanel.PositionCallback() { public void
					 * setPosition(int offsetWidth, int offsetHeight) { int left
					 * = (Window.getClientWidth() - offsetWidth) / 3; int top =
					 * (Window.getClientHeight() - offsetHeight) / 3;
					 * popup.setPopupPosition(left, top); } });
					 */
					TextField label = new TextField(
							"From: Target\nTo: Macys\nGo Straight\nTurn right at corner\nTurn left");
					label
							.setValue("From: Target\nTo: Macys\nGo Straight\nTurn right at corner\nTurn left");
					popup.add(label);
					popup.setTitle("Detailed Directions");
					// topPanel.add(popup);
					popup.show();
					directions.setDown(false);
				}
			});
			directionPanel.add(directions);
			topPanel.add(directionPanel);
		}

		return topPanel;
	}

	// create map
	private Panel buildMap() {
		Panel mapPanel = new Panel();
		mapPanel.setLayout(new FitLayout());
		Image mapImage = null;
		if (currentDirection == NO_DIRECTION) {
			if (zoomedIn && showLocation) {
				mapImage = new Image("images/TargetPlazaLocation.jpg");
			} else if (zoomedIn && !showLocation) {
				if (currentLocation == CENTER) {
					mapImage = new Image("images/TargetPlaza.jpg");
				} else if (currentLocation == UP) {
					mapImage = new Image("images/TargetPlazaUp.jpg");
				} else if (currentLocation == DOWN) {
					mapImage = new Image("images/TargetPlazaDown.jpg");
				} else if (currentLocation == LEFT) {
					mapImage = new Image("images/TargetPlazaLeft.jpg");
				} else if (currentLocation == RIGHT) {
					mapImage = new Image("images/TargetPlazaRight.jpg");
				}
			} else if (!zoomedIn && showLocation) {
				mapImage = new Image("images/wholeMapLocation.jpg");
			} else if (!zoomedIn && !showLocation) {
				mapImage = new Image("images/wholeMap.jpg");
			}
		} else if (currentDirection == TARGET_MACY) {
			mapImage = new Image("images/TargetPlazaMacysShortest.jpg");
		} else if (currentDirection == TARGET_MACY_30) {
			mapImage = new Image("images/TargetPlazaMacys30Mins.jpg");
		}
		mapPanel.add(mapImage);
		return mapPanel;
	}

	private void buildMapPanel() {
		TabPanel tabs = new TabPanel();
		tabs.setResizeTabs(false);
		tabs.setBorder(false);

		buildMapTabScrolling();
		directionsList.setDisabled(true);
		directionsList.setHtml(nohtml);

		tabs.add(mapScrollingTab);
		tabs.add(directionsList);

		mapPanel.add(tabs);
		mapPanel.setBorder(false);
	}

	private void buildMapTabScrolling() {
		mapScrollingTab.setLayout(new FitLayout());
		mapScrollingTab.setBorder(false);
		mapScrollingTab.setHeight(HEIGHT - 104);

		Panel wrapper = new Panel();
		wrapper.setLayout(new FitLayout());
		wrapper.setBorder(false);
		wrapper.setHeader(false);

		mapScroll.setAlwaysShowScrollBars(true);
		currentImage = getMapImage();
		mapScroll.add(currentImage);
		setScrolls();

		final String in = "+ Zoom in +";
		final String out = "- Zoom out -";

		zoomButton.setText(in);
		zoomButton.setEnableToggle(true);
		zoomButton.setPressed(false);
		zoomButton.setMinWidth(290);

		QuickTipsConfig tipsConfig = new QuickTipsConfig();
		tipsConfig.setText("Press to toggle zoom level");
		tipsConfig.setTitle("Zoom");
		zoomButton.setTooltip(tipsConfig);

		zoomButton.addListener(new ButtonListenerAdapter() {
			public void onClick(Button button, EventObject e) {
				System.out.println("zoom toggle pressed");
				// button actions
				if (button.isPressed()) {
					zoomedIn = true;
					button.setText(out);
				} else {
					zoomedIn = false;
					button.setText(in);
				}
				updateMap();
			}
		});

		wrapper.setTopToolbar(zoomButton);
		wrapper.add(mapScroll);

		mapScrollingTab.add(wrapper);
	}

	private void setScrolls() {
		int vPos = 0;
		int hPos = 0;
		if (showDirection || showLocation) {
			if (zoomedIn) {
				vPos = 300;
				hPos = 120;
			} else {
				vPos = 120;
				hPos = 0;
			}
		} else {
			if (zoomedIn) {
				vPos = 350;
				hPos = 250;
			} else {
				vPos = 75;
				hPos = 80;
			}
		}

		System.out.println("h=" + hPos + " v=" + vPos);

		mapScroll.setScrollPosition(vPos);
		mapScroll.setHorizontalScrollPosition(hPos);
	}

	private Image getMapImage() {
		// logic on which map to show
		if (zoomedIn) {
			if (showLocation) {
				if (showDirection) {
					if (showLong) {
						return inLocDirLong;
					} else {
						return inLocDirShort;
					}
				} else {
					return inLoc;
				}
			} else {
				if (showDirection) {
					if (showLong) {
						return inDirLong;
					} else {
						return inDirShort;
					}
				} else {
					return inNormal;
				}
			}
		} else {
			if (showLocation) {
				if (showDirection) {
					if (showLong) {
						return outLocDirLong;
					} else {
						return outLocDirShort;
					}
				} else {
					return outLoc;
				}
			} else {
				if (showDirection) {
					if (showLong) {
						return outDirLong;
					} else {
						return outDirShort;
					}
				} else {
					return outNormal;
				}
			}
		}
	}

	private void updateSaveGoButtons() {
		if(!saveButtonsReady) {
			return;
		}
		if (toCombo.isValid() && toCombo.getValue() != null
				&& fromCombo.isValid() && fromCombo.getValue() != null) {
			go.setEnabled(true);
			save.setEnabled(true);
		} else {
			go.setEnabled(false);
			save.setEnabled(false);
		}
		System.out.println("Updated save/go buttons");
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

	private void showPanel(Panel panel) {
		mainPannel.removeAll();
		mainPannel.add(panel);
		panel.setVisible(true);
		// updates the gui
		mainPannel.doLayout();
	}

}
