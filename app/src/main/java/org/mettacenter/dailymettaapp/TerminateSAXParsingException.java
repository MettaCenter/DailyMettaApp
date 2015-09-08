package org.mettacenter.dailymettaapp;

import org.xml.sax.SAXException;

/*
    http://stackoverflow.com/questions/1345293/how-to-stop-parsing-xml-document-with-sax-at-any-time
    http://stackoverflow.com/questions/2964315/java-sax-parser-how-to-manually-break-parsing
     */
public class TerminateSAXParsingException
        extends SAXException {
    public TerminateSAXParsingException(){
        super();
    }
}
