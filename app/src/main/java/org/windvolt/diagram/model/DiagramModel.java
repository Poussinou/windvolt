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
package org.windvolt.diagram.model;

public class DiagramModel extends MetaModel {

    /* --------------------------------windvolt-------------------------------- */

    String title = "<title>";
    public String getTitle() { return title; }
    public void setTitle(String value) { title = value; }

    String subject = "<subject>";
    public String getSubject() { return subject; }
    public void setSubject(String value) { subject = value; }

    String symbol = "";
    public String getSymbol() { return symbol; }
    public void setSymbol(String value) { symbol = value; }

    String address = "";
    public String getAdress() { return address; }
    public void setAddress(String vlaue) { address = vlaue; }

    String children = "";
    public String getChildren() { return children; }
    public void setChildren(String value) { children = value; }

}
