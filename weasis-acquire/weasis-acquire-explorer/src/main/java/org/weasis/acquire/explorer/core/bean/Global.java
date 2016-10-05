/*******************************************************************************
 * Copyright (c) 2016 Weasis Team and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Nicolas Roduit - initial API and implementation
 *******************************************************************************/
package org.weasis.acquire.explorer.core.bean;

import java.util.Optional;
import java.util.function.Consumer;

import org.dcm4che3.data.Tag;
import org.dcm4che3.util.UIDUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.weasis.core.api.media.data.TagW;
import org.weasis.dicom.codec.TagD;

public class Global extends AbstractTagable {

    public void init(Document xml) {
        tags.put(TagD.get(Tag.StudyInstanceUID), UIDUtils.createUID());
        Optional.of(xml).map(o -> o.getDocumentElement()).ifPresent(init);
    }

    private final Consumer<Element> init = e -> {
        NodeList nodes = e.getChildNodes();
        if (nodes != null) {
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                setTag(node);
            }
        }
    };

    private void setTag(Node node) {
        if (node != null) {
            TagW tag = TagD.get(node.getNodeName());
            if (tag != null) {
                tag.readValue(node.getTextContent(), this);
            }
        }
    }

    public boolean containSameTagsValues(Document xml) {
        if (xml == null) {
            throw new IllegalArgumentException("empty xml parameter");
        }

        NodeList nodes = xml.getChildNodes();
        if (nodes == null) {
            return false;
        }

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node != null) {
                TagW tag = TagD.get(node.getNodeName());
                if (tag != null) {
                    Object tagVal = getTagValue(tag);
                    String xmlTagContent = node.getTextContent();
                    if (xmlTagContent != null && xmlTagContent.trim().length() == 0) {
                        xmlTagContent = null;
                    }
                    if ((tagVal == null && xmlTagContent == null) || (tagVal != null && tagVal.equals(xmlTagContent))) {
                        continue;
                    }
                }
                return false;
            }
        }
        return true;

    }

    @Override
    public String toString() {
        TagW name = TagD.get(Tag.PatientName);
        return name.getFormattedTagValue(getTagValue(name), null);
    }
}
