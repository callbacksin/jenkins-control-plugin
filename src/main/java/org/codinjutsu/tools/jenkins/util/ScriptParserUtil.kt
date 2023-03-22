package org.codinjutsu.tools.jenkins.util

import org.w3c.dom.Document
import org.xml.sax.InputSource
import java.io.FileNotFoundException
import java.io.StringReader
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult


private fun getXMLDocument(output: String): Document {
    val dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
    val xmlInput = InputSource(StringReader(output))
    return dBuilder.parse(xmlInput)
}

fun hasScriptXMLTag(config: String): Boolean {
    val doc = getXMLDocument(config)
    return doc.getElementsByTagName("script").length != 0
}


fun getScriptContent(output: String): String {
    val doc = getXMLDocument(output)
    val script = doc.getElementsByTagName("script")
    return script.item(0).textContent
}

fun updateContentOfTag(tagName: String, xml: String, newContent: String): String {
    val doc = getXMLDocument(xml)
    doc.getElementsByTagName(tagName).item(0).textContent = newContent
    val tf: TransformerFactory = TransformerFactory.newInstance()
    val transformer: Transformer = tf.newTransformer()
    val writer = StringWriter()
    transformer.transform(DOMSource(doc), StreamResult(writer))
    val output = writer.toString()
    return output
}

fun substituteDescriptionAndGetNewJobConfig(desc: String): String? {
    var fileContent: String? = object {}.javaClass.getResource("/declarative_config.xml")?.readText()
        ?: throw FileNotFoundException("resource declarative_config.xml not found")
    fileContent = fileContent?.replace("descriptionContentString", desc)
    return fileContent
}