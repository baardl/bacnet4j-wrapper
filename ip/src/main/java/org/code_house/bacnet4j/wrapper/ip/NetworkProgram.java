/*
 * (C) Copyright 2017 Code-House and others.
 *
 * bacnet4j-wrapper is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 *     https://www.gnu.org/licenses/gpl-3.0.txt
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.code_house.bacnet4j.wrapper.ip;

import org.code_house.bacnet4j.wrapper.api.BypassBacnetConverter;
import org.code_house.bacnet4j.wrapper.api.Device;
import org.code_house.bacnet4j.wrapper.api.Property;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Simple class to run discovery across all network interfaces, fetch discovered devices properties and it's values.
 *
 * @author Łukasz Dywicki &lt;luke@code-house.org&gt;
 */
public class NetworkProgram {

    private final Visitor visitor;

    NetworkProgram(Visitor visitor) {
        this.visitor = visitor;
    }

    public void run(String[] args) throws Exception {List<String> interfaceIPs = new ArrayList<>();
        if (args.length > 0) {
            String broadcasts = args[0].trim();
            Arrays.stream(broadcasts.split(","))
                .map(String::trim)
                .forEach(interfaceIPs::add);
        }

        Long timeout = 30L;
        if (args.length > 1) {
            timeout = Long.parseLong(args[1]);
        }

        int deviceId = 1339;
        if (args.length > 2) {
            deviceId = Integer.parseInt(args[2]);
        }

        if (interfaceIPs.isEmpty()) {
            // For each interface ...
            System.out.println("Fetching network interfaces");
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface networkInterface = en.nextElement();
                if (!networkInterface.isLoopback()) {

                    // .. and for each address ...
                    for (Iterator<InterfaceAddress> it = networkInterface.getInterfaceAddresses().iterator(); it.hasNext(); ) {

                        // ... get IP and Subnet
                        InterfaceAddress interfaceAddress = it.next();

                        InetAddress broadcast = interfaceAddress.getBroadcast();
                        if (broadcast != null) {
                            interfaceIPs.add(broadcast.getHostAddress());
                        }
                    }
                }
            }
        }

        if (interfaceIPs.isEmpty()) {
            System.out.println("No broadcast interfaces found");
        }

        for (String broadcast : interfaceIPs) {
            System.out.println("Device id " + deviceId);
            System.out.println("Fetching devices for " + broadcast + " address with " + timeout + " second timeout");
            BacNetIpClient client = new BacNetIpClient(broadcast, deviceId);
            client.start();

            Set<Device> devices = client.discoverDevices(TimeUnit.SECONDS.toMillis(timeout));
            if (devices.isEmpty()) {
                System.out.println(" => No Devices found");
            } else {
                for (Device device : devices) {
                    if (visitor.visit(device) == Visitor.Flag.CONTNUE) {
                        List<Property> properties = client.getDeviceProperties(device);
                        if (properties.isEmpty()) {
                            System.out.println("      => No properties found");
                        } else {
                            for (Property property : properties) {
                                if (visitor.visit(property) == Visitor.Flag.CONTNUE) {
                                    visitor.visit(client.getPropertyValue(property, new BypassBacnetConverter()));
                                }
                            }
                        }
                    }
                }
            }

            client.stop();
        }

        System.out.println("Discovery complete");
    }
}
