package com.vaadin.tapio.googlemaps.demo;

import java.util.ArrayList;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.GoogleMapControl;
import com.vaadin.tapio.googlemaps.client.GoogleMapInfoWindow;
import com.vaadin.tapio.googlemaps.client.GoogleMapMarker;
import com.vaadin.tapio.googlemaps.client.GoogleMapPolygon;
import com.vaadin.tapio.googlemaps.client.GoogleMapPolyline;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.events.InfoWindowClosedListener;
import com.vaadin.tapio.googlemaps.client.events.MapMoveListener;
import com.vaadin.tapio.googlemaps.client.events.MarkerClickListener;
import com.vaadin.tapio.googlemaps.client.events.MarkerDragListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * Google Maps UI for testing and demoing.
 * 
 * @author Tapio Aali <tapio@vaadin.com>
 */
@SuppressWarnings("serial")
public class DemoUI extends UI {

    private GoogleMap googleMap;
    private GoogleMapMarker kakolaMarker = new GoogleMapMarker(
            "DRAGGABLE: Kakolan vankila", new LatLon(60.44291, 22.242415),
            true, null);
    private final String apiKey = "";

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class, widgetset = "com.vaadin.tapio.googlemaps.demo.DemoWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        setContent(content);

        TabSheet tabs = new TabSheet();
        tabs.setSizeFull();
        content.addComponent(tabs);

        VerticalLayout tab1 = new VerticalLayout();
        tab1.setSizeFull();
        tab1.setCaption("MAP");

        googleMap = new GoogleMap(new LatLon(60.440963, 22.25122), 10.0, apiKey);
        googleMap.setSizeFull();
        kakolaMarker.setAnimationEnabled(false);
        googleMap.addMarker(kakolaMarker);
        googleMap.addMarker("DRAGGABLE: Paavo Nurmi Stadion", new LatLon(
                60.442423, 22.26044), true, "VAADIN/1377279006_stadium.png");
        googleMap.addMarker("NOT DRAGGABLE: Iso-Heikkilä", new LatLon(
                60.450403, 22.230399), false, null);
        googleMap.setMinZoom(4.0);
        googleMap.setMaxZoom(16.0);
        tab1.addComponent(googleMap);
        tab1.setExpandRatio(googleMap, 1.0f);

        Panel console = new Panel();
        console.setHeight("100px");
        final CssLayout consoleLayout = new CssLayout();
        console.setContent(consoleLayout);
        tab1.addComponent(console);

        HorizontalLayout buttonLayoutRow1 = new HorizontalLayout();
        buttonLayoutRow1.setHeight("26px");
        tab1.addComponent(buttonLayoutRow1);

        HorizontalLayout buttonLayoutRow2 = new HorizontalLayout();
        buttonLayoutRow2.setHeight("26px");
        tab1.addComponent(buttonLayoutRow2);

        googleMap.addMarkerClickListener(new MarkerClickListener() {
            @Override
            public void markerClicked(GoogleMapMarker clickedMarker) {
                Label consoleEntry = new Label("Marker \""
                        + clickedMarker.getCaption() + "\" at ("
                        + clickedMarker.getPosition().getLat() + ", "
                        + clickedMarker.getPosition().getLon() + ") clicked.");
                consoleLayout.addComponent(consoleEntry, 0);
            }
        });

        googleMap.addMapMoveListener(new MapMoveListener() {
            @Override
            public void mapMoved(double zoomLevel, LatLon center,
                    LatLon boundsNE, LatLon boundsSW) {
                Label consoleEntry = new Label("Map moved to ("
                        + center.getLat() + ", " + center.getLon() + "), zoom "
                        + zoomLevel + ", boundsNE: (" + boundsNE.getLat()
                        + ", " + boundsNE.getLon() + "), boundsSW: ("
                        + boundsSW.getLat() + ", " + boundsSW.getLon() + ")");
                consoleLayout.addComponent(consoleEntry, 0);
            }
        });

        googleMap.addMarkerDragListener(new MarkerDragListener() {
            @Override
            public void markerDragged(GoogleMapMarker draggedMarker,
                    LatLon oldPosition) {
                Label consoleEntry = new Label("Marker \""
                        + draggedMarker.getCaption() + "\" dragged from ("
                        + oldPosition.getLat() + ", " + oldPosition.getLon()
                        + ") to (" + draggedMarker.getPosition().getLat()
                        + ", " + draggedMarker.getPosition().getLon() + ")");
                consoleLayout.addComponent(consoleEntry, 0);
            }
        });

        googleMap.addInfoWindowClosedListener(new InfoWindowClosedListener() {

            @Override
            public void infoWindowClosed(GoogleMapInfoWindow window) {
                Label consoleEntry = new Label("InfoWindow \""
                        + window.getContent() + "\" closed");
                consoleLayout.addComponent(consoleEntry, 0);
            }
        });

        Button moveCenterButton = new Button(
                "Move over Luonnonmaa (60.447737, 21.991668), zoom 12",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        googleMap.setCenter(new LatLon(60.447737, 21.991668));
                        googleMap.setZoom(12.0);
                    }
                });
        buttonLayoutRow1.addComponent(moveCenterButton);

        Button limitCenterButton = new Button(
                "Limit center between (60.619324, 22.712753), (60.373484, 21.945083)",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        googleMap.setCenterBoundLimits(new LatLon(60.619324,
                                22.712753), new LatLon(60.373484, 21.945083));
                        event.getButton().setEnabled(false);
                    }
                });
        buttonLayoutRow1.addComponent(limitCenterButton);

        Button limitVisibleAreaButton = new Button(
                "Limit visible area between (60.494439, 22.397835), (60.373484, 21.945083)",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        googleMap.setVisibleAreaBoundLimits(new LatLon(
                                60.494439, 22.397835), new LatLon(60.420632,
                                22.138626));
                        event.getButton().setEnabled(false);
                    }
                });
        buttonLayoutRow1.addComponent(limitVisibleAreaButton);

        Button zoomToBoundsButton = new Button("Zoom to bounds",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        googleMap.fitToBounds(new LatLon(60.45685853323144,
                                22.320034754486073), new LatLon(
                                60.4482979242303, 22.27887893936156));

                    }
                });
        buttonLayoutRow1.addComponent(zoomToBoundsButton);
        Button addPolyOverlayButton = new Button("Add overlay over Luonnonmaa",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        ArrayList<LatLon> points = new ArrayList<LatLon>();
                        points.add(new LatLon(60.484715, 21.923706));
                        points.add(new LatLon(60.446636, 21.941387));
                        points.add(new LatLon(60.422496, 21.99546));
                        points.add(new LatLon(60.427326, 22.06464));
                        points.add(new LatLon(60.446467, 22.064297));

                        GoogleMapPolygon overlay = new GoogleMapPolygon(points,
                                "#ae1f1f", 0.8, "#194915", 0.5, 3);
                        googleMap.addPolygonOverlay(overlay);
                        event.getButton().setEnabled(false);
                    }
                });
        buttonLayoutRow2.addComponent(addPolyOverlayButton);

        Button addPolyLineButton = new Button("Draw line from Turku to Raisio",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        ArrayList<LatLon> points = new ArrayList<LatLon>();
                        points.add(new LatLon(60.448118, 22.253738));
                        points.add(new LatLon(60.455144, 22.24198));
                        points.add(new LatLon(60.460222, 22.211939));
                        points.add(new LatLon(60.488224, 22.174602));
                        points.add(new LatLon(60.486025, 22.169195));

                        GoogleMapPolyline overlay = new GoogleMapPolyline(
                                points, "#d31717", 0.8, 10);
                        googleMap.addPolyline(overlay);
                        event.getButton().setEnabled(false);
                    }
                });
        buttonLayoutRow2.addComponent(addPolyLineButton);
        Button addPolyLineButton2 = new Button(
                "Draw line from Turku to Raisio2", new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        ArrayList<LatLon> points2 = new ArrayList<LatLon>();
                        points2.add(new LatLon(60.448118, 22.253738));
                        points2.add(new LatLon(60.486025, 22.169195));
                        GoogleMapPolyline overlay2 = new GoogleMapPolyline(
                                points2, "#d31717", 0.8, 10);
                        googleMap.addPolyline(overlay2);
                        event.getButton().setEnabled(false);
                    }
                });
        buttonLayoutRow2.addComponent(addPolyLineButton2);
        Button changeToTerrainButton = new Button("Change to terrain map",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        googleMap.setMapType(GoogleMap.MapType.Terrain);
                        event.getButton().setEnabled(false);
                    }
                });
        buttonLayoutRow2.addComponent(changeToTerrainButton);

        Button changeControls = new Button("Remove street view control",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        googleMap.removeControl(GoogleMapControl.StreetView);
                        event.getButton().setEnabled(false);
                    }
                });
        buttonLayoutRow2.addComponent(changeControls);

        Button addInfoWindowButton = new Button(
                "Add InfoWindow to Kakola marker", new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        GoogleMapInfoWindow window = new GoogleMapInfoWindow(
                                "Kakola used to be a provincial prison.",
                                kakolaMarker);
                        googleMap.openInfoWindow(window);
                    }
                });
        buttonLayoutRow2.addComponent(addInfoWindowButton);

        Button moveMarkerButton = new Button("Move kakola marker",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        kakolaMarker.setPosition(new LatLon(60.3, 22.242415));
                        googleMap.addMarker(kakolaMarker);
                    }
                });
        buttonLayoutRow2.addComponent(moveMarkerButton);

        tabs.addTab(tab1);

        Label tab2 = new Label("Tab2!");
        tab2.setCaption("Tab 2");
        tabs.addTab(tab2);
    }
}