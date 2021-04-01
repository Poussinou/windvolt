/*
    This file is part of windvolt.org.

    Copyright (c) 2020 Max Sumer

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.windvolt.system.device_store;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class XMLDeviceModelStore {
    final String ns = null;
    static String xml_file = "devices.xml";

    Context devices_context;

    List<DeviceModel> deviceStore = new ArrayList<>();

    private ArrayAdapter ListViewAdapter = null;


    private String error_message = "okay";
    public String getErrorMessage() { return error_message; }

    private int xpower = 0;
    public String getPowerCapability() { return Integer.toString(xpower); }

    public XMLDeviceModelStore(@NonNull Context context) {
        devices_context = context;
    }

    public ArrayAdapter createListViewAdapter(int resourceId, int textViewResourceId) {
        ListViewAdapter = new ArrayAdapter(devices_context, resourceId, textViewResourceId, new ArrayList()) {

        };

        return getListViewAdapter();
    }
    public ArrayAdapter getListViewAdapter() { return ListViewAdapter; }



    public boolean initialize() {

        if (getDeviceCount() == 0) {
            addDevice("Alcatel X1", "0", "100");
        }

        return true;
    }
    public int getDeviceCount() { return deviceStore.size(); }

    public void addDevice(String name, String type, String power) {
        DeviceModel device_model = new DeviceModel();
        device_model.setName(name);
        device_model.setType(type);
        device_model.setPower(power);

        deviceStore.add(device_model);
        syncAdapter();
    }
    public void removeDevice(int position) {
        int size = deviceStore.size();
        if (position > size - 1) {
            return;
        }

        deviceStore.remove(position);
        syncAdapter();
    }

    private void syncAdapter() {

        xpower = 0;

        ArrayAdapter adapter = getListViewAdapter();

        if (adapter != null) {
            adapter.clear();

            int size = deviceStore.size();
            for (int pos=0; pos<size; pos++) {
                DeviceModel device_model = deviceStore.get(pos);

                String name = device_model.getName();

                String type;
                switch (device_model.getType()) {
                    case "0":
                        type = "mobile ";
                        break;

                    case "1":
                        type = "ecar ";
                        break;

                    case "2":
                        type = "household ";
                        break;

                    default:
                        type = "other ";
                }

                String power = device_model.getPower();
                xpower = xpower + Integer.parseInt(power);

                String desc = "(" + type + "  " + power + " Wh)";

                int l = 24 - name.length() - desc.length();

                String d = Integer.toString(pos) + " " + name + " " + desc;
                adapter.add(d);
            }

        }
    }

    public boolean isUpdateAllowed() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(devices_context);
        boolean updates = sharedPreferences.getBoolean("updates", false);

        return updates;
    }
    public boolean isNotificationAllowed() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(devices_context);
        boolean notifications = sharedPreferences.getBoolean("notifications", false);

        return notifications;
    }





    /*
    xml file operations
     */

    public boolean loadModel() {
        error_message = "okay";

        try {

            FileInputStream fileInputStream = devices_context.openFileInput(xml_file);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(fileInputStream);
            Element root = document.getDocumentElement();
            root.normalize();

            NodeList devices = root.getElementsByTagName("device");
            int size = devices.getLength();

            for (int position=0; position<size; position++) {
                Node device = devices.item(position);

                if (device.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) device;

                    String name = element.getElementsByTagName("name").item(0).getTextContent();

                    String type = element.getElementsByTagName("type").item(0).getTextContent();

                    String power = element.getElementsByTagName("power").item(0).getTextContent();

                    addDevice(name, type, power);
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            error_message = exception.getMessage();

            return false;
        }

        return true;
    }

    public boolean saveModel() {
        error_message = "okay";

        try {
            FileOutputStream fileOutputStream = devices_context.openFileOutput(xml_file, Context.MODE_PRIVATE);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.newDocument();
            //document.setXmlStandalone(false);

            Element root = document.createElement("devices");
            document.appendChild(root);

            int size = deviceStore.size();
            for (int position=0; position<size; position++) {
                DeviceModel deviceModel = deviceStore.get(position);

                // create device
                Element device = document.createElement("device");
                root.appendChild(device);

                // create properties

                Element name = document.createElement("name");
                name.appendChild(document.createTextNode(deviceModel.getName()));
                device.appendChild(name);

                Element type = document.createElement("type");
                type.appendChild(document.createTextNode(deviceModel.getType()));
                device.appendChild(type);

                Element power = document.createElement("power");
                power.appendChild(document.createTextNode(deviceModel.getPower()));
                device.appendChild(power);


            }

            // save
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            //transformerFactory.setAttribute("indent-number", 1);

            Transformer transformer = transformerFactory.newTransformer();

            //transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "1");

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(fileOutputStream);
            transformer.transform(source, result);


        } catch (Exception exception) {
            exception.printStackTrace();
            error_message = exception.getMessage();

            return false;
        }

        if (isUpdateAllowed()) {
            // TODO update online catalog

        }

        return true;
    }

}
