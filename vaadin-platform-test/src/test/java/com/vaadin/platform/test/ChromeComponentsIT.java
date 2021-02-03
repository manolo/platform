/*
 * Copyright 2000-2018 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.platform.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.WrapsElement;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.checkbox.testbench.CheckboxElement;
import com.vaadin.flow.component.combobox.testbench.ComboBoxElement;
import com.vaadin.flow.component.datepicker.testbench.DatePickerElement;
import com.vaadin.flow.component.formlayout.testbench.FormLayoutElement;
import com.vaadin.flow.component.grid.testbench.GridElement;
import com.vaadin.flow.component.html.testbench.DivElement;
import com.vaadin.flow.component.ironlist.testbench.IronListElement;
import com.vaadin.flow.component.menubar.testbench.MenuBarElement;
import com.vaadin.flow.component.notification.testbench.NotificationElement;
import com.vaadin.flow.component.orderedlayout.testbench.HorizontalLayoutElement;
import com.vaadin.flow.component.orderedlayout.testbench.VerticalLayoutElement;
import com.vaadin.flow.component.progressbar.testbench.ProgressBarElement;
import com.vaadin.flow.component.radiobutton.testbench.RadioButtonElement;
import com.vaadin.flow.component.radiobutton.testbench.RadioButtonGroupElement;
import com.vaadin.flow.component.select.testbench.SelectElement;
import com.vaadin.flow.component.splitlayout.testbench.SplitLayoutElement;
import com.vaadin.flow.component.tabs.testbench.TabElement;
import com.vaadin.flow.component.tabs.testbench.TabsElement;
import com.vaadin.flow.component.textfield.testbench.PasswordFieldElement;
import com.vaadin.flow.component.textfield.testbench.TextAreaElement;
import com.vaadin.flow.component.textfield.testbench.TextFieldElement;
import com.vaadin.flow.component.upload.testbench.UploadElement;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.annotations.BrowserConfiguration;
import com.vaadin.testbench.parallel.Browser;

public class ChromeComponentsIT extends AbstractPlatformTest {

    @Override
    protected String getTestPath() {
        return "/prod-mode/";
    }

    @Test
    public void openPageNoClientSideError() {
        checkLogsForErrors();
    }

    @Test
    public void buttonIsRenderedAndRecievesClicks() {
        ButtonElement button = $(ButtonElement.class).first();
        assertElementRendered(button);
        button.click();
        assertLog("Clicked button");
    }

    @Test
    public void checkboxIsRenderedAndRecievesValueChangeEvent() {
        CheckboxElement checkbox = $(CheckboxElement.class).first();
        TestBenchElement htmlButton = checkbox.$("input")
                .attribute("type", "checkbox").first();
        assertElementRendered(htmlButton);

        checkbox.click();

        assertLog("Checkbox value changed from 'false' to 'true'");
    }

    @Test
    public void checkboxGroupIsRenderedAndRecievesValueChangeEvent() {
        TestBenchElement checkboxGroup = $("vaadin-checkbox-group").first();

        TestBenchElement groupField = checkboxGroup.$(DivElement.class)
                .attribute("part", "group-field").first();
        assertElementRendered(groupField);

        checkboxGroup.$(CheckboxElement.class).first().click();

        assertLog("CheckboxGroup value changed from '[]' to '[foo]'");
    }

    @Test
    public void comboboxIsRenderedAndRecievesValueChangeEvent() {
        ComboBoxElement comboBox = $(ComboBoxElement.class).first();

        TextFieldElement textField = comboBox.$(TextFieldElement.class)
                .id("input");
        assertElementRendered(textField);

        comboBox.$(TestBenchElement.class).id("toggleButton").click();

        WebElement dropDown = $("vaadin-combo-box-overlay").id("overlay");

        assertElementRendered(dropDown);

        getCommandExecutor().executeScript("arguments[0].value='1'", comboBox);

        assertLog("ComboBox value changed from 'null' to 'First'");
    }

    @Test
    public void datePickerIsRenderedAndRecievesValueChangeEvent() {
        DatePickerElement datePicker = $(DatePickerElement.class).first();

        TestBenchElement textField = datePicker
                .$("vaadin-date-picker-text-field").id("input");
        assertElementRendered(textField);

        datePicker.$(DivElement.class).attribute("part", "toggle-button").first().click();

        WebElement dropDown = $("vaadin-date-picker-overlay").id("overlay");

        assertElementRendered(dropDown);

        getCommandExecutor().executeScript("arguments[0].value='2018-12-04'",
                datePicker);

        assertLog("DatePicker value changed from null to 2018-12-04");
    }

    @Test
    public void timePickerIsRenderedAndRecievesValueChangeEvent() {
        TestBenchElement timePicker = $("vaadin-time-picker").first();

        TestBenchElement textField = timePicker
                .$("vaadin-time-picker-text-field").first();
        assertElementRendered(textField);

        timePicker.$("span").attribute("part", "toggle-button").first().click();

        WebElement dropDown = $("vaadin-combo-box-overlay").first();

        assertElementRendered(dropDown);

        getCommandExecutor().executeScript("arguments[0].value='01:37'",
                timePicker);

        assertLog("TimePicker value changed from null to 01:37");
    }

    @Test
    public void selectIsRenderedAndReceivesValueChangeEvent() {
        SelectElement select = $(SelectElement.class).first();

        select.$(DivElement.class).attribute("part", "toggle-button").first().click();

        WebElement dropDown = $("vaadin-select-overlay").first();

        assertElementRendered(dropDown);

        getCommandExecutor().executeScript("arguments[0].value='1'", select);

        assertLog("Select value changed from null to Spring");
    }

    @Test
    public void gridIsRenderedAndRecievesSelectionEvents() {
        GridElement grid = $(GridElement.class).first();

        assertElementRendered(grid);

        TestBenchElement table = grid.$("table").id("table");

        assertElementRendered(table);

        Assert.assertEquals("Some", grid.getCell(0, 1).getText());
        Assert.assertEquals("Data", grid.getCell(0, 2).getText());

        Assert.assertEquals("Second", grid.getCell(1, 1).getText());
        Assert.assertEquals("Row", grid.getCell(1, 2).getText());

        grid.getCell(0, 1).click();

        assertLog("Grid selection changed to 'Optional[{bar=Data, foo=Some}]'");
    }

    @Test
    public void gridContextMenuRenderedAndReceivesTargetItem() {
        GridElement grid = $(GridElement.class).first();
        grid.getCell(1, 0).click();

        waitUntil(ExpectedConditions.visibilityOfElementLocated(
                By.tagName("vaadin-context-menu-overlay")));
        TestBenchElement contextMenuItem = $("vaadin-context-menu-overlay")
                .first().$("vaadin-context-menu-item").first();
        Assert.assertEquals("foo", contextMenuItem.getText());

        contextMenuItem.click();
        assertLog("GridContextMenu on item Second");
    }

    @Test
    public void iconsAreRendered() {
        TestBenchElement hIcon = $("iron-icon").get(1);
        TestBenchElement vIcon = $("iron-icon").get(2);

        assertElementRendered(hIcon);
        assertElementRendered(vIcon);

        TestBenchElement svg = hIcon.$("svg").first();
        assertElementRendered(svg);

        svg = vIcon.$("svg").first();
        assertElementRendered(svg);
    }

    @Test
    public void ironListIsRendered() {
        IronListElement ironList = $(IronListElement.class).first();

        TestBenchElement itemsContainer = ironList.$(DivElement.class).id("items");
        assertElementRendered(itemsContainer);

        List<TestBenchElement> items = ironList.$("span").all();
        Assert.assertFalse(items.isEmpty());
        items.stream().forEach(this::assertElementRendered);

        for (int i = 0; i < items.size(); i++) {
            Assert.assertEquals("Item " + i, items.get(i).getText());
        }
    }

    @Test
    public void progressBarIsRendered() {
        ProgressBarElement ironList = $(ProgressBarElement.class).first();

        TestBenchElement bar = ironList.$(DivElement.class).attribute("part", "bar")
                .first();
        assertElementRendered(bar);

        TestBenchElement value = bar.$(DivElement.class).attribute("part", "value")
                .first();

        assertElementRendered(value);

        Assert.assertTrue(
                value.getSize().getWidth() < bar.getSize().getWidth());
    }

    @Test
    public void radioButtonGroupIsRenderedAndRecievesValueChangeEvents() {
        RadioButtonGroupElement radioButtonGroup = $(
                RadioButtonGroupElement.class).first();

        TestBenchElement groupField = radioButtonGroup.$(DivElement.class)
                .attribute("part", "group-field").first();
        assertElementRendered(groupField);

        List<RadioButtonElement> radioButtons = radioButtonGroup
                .$(RadioButtonElement.class).all();
        Assert.assertEquals(5, radioButtons.size());

        radioButtons.stream().forEach(this::assertElementRendered);

        for (int i = 0; i < 5; i++) {
            Assert.assertEquals("Item " + i, radioButtons.get(i).getText());
        }

        radioButtons.get(0).click();

        assertLog("RadioButtonGroup value changed from null to Item 0");
    }

    @Test
    public void textFieldIsRenderedAndRecievesValueChangeEvents() {
        assertTextComponent($(TextFieldElement.class).first(), "input",
                "TextField value changed from to foo");
    }

    @Test
    public void passwordFieldIsRenderedAndRecievesValueChangeEvents() {
        assertTextComponent($(PasswordFieldElement.class).first(),
                "input", "PasswordField value changed from to foo");
    }

    @Test
    public void textAreaIsRenderedAndRecievesValueChangeEvents() {
        assertTextComponent($(TextAreaElement.class).first(), "textarea",
                "TextArea value changed from to foo");
    }

    @Test
    public void uploadIsRenderedAndUploadFile() throws IOException {
        UploadElement upload = $(UploadElement.class).first();

        ButtonElement uploadButton = upload.$(ButtonElement.class).first();
        assertElementRendered(uploadButton);

        TestBenchElement dropLabel = upload.$(TestBenchElement.class)
                .id("dropLabelContainer");

        assertElementRendered(dropLabel);

        File tempFile = createTempFile();
        fillPathToUploadInput(tempFile.getPath());

        assertLog("Upload received file text/plain with text foo");
    }

    @Test
    public void dialogIsRendered() {
        $(ButtonElement.class).id("open-dialog").click();
        TestBenchElement dialogOverlay = $("vaadin-dialog-overlay")
                .id("overlay");

        TestBenchElement content = dialogOverlay.$(TestBenchElement.class)
                .id("content");

        assertElementRendered(content);

        TestBenchElement contentComponent = dialogOverlay
                .$("flow-component-renderer").first().$(DivElement.class).first();

        Assert.assertEquals("This is the contents of the dialog",
                contentComponent.getText());
    }

    @Test
    public void notificationIsRendered() {
        waitUntil(driver -> $(NotificationElement.class).all().size() > 0);
        NotificationElement notification = $(NotificationElement.class).first();

        TestBenchElement card = (TestBenchElement) notification.getContext();
        assertElementRendered(card);

        waitUntil(driver -> "Hello".equals(notification.getText()));
    }

    @Test
    public void formLayoutIsRendered() {
        FormLayoutElement formLayoutElement = $(FormLayoutElement.class).first();

        TestBenchElement layoutElement = formLayoutElement
                .$(TestBenchElement.class).id("layout");

        assertElementRendered(layoutElement);

        List<TextFieldElement> textFields = formLayoutElement
                .$(TextFieldElement.class).all();

        Assert.assertEquals(6, textFields.size());
    }

    @Test
    public void verticalLayoutIsRendered() {
        VerticalLayoutElement verticalLayoutElement = $(
                VerticalLayoutElement.class).id("verticallayout");

        assertElementRendered(verticalLayoutElement);

        List<ButtonElement> buttons = verticalLayoutElement
                .$(ButtonElement.class).all();

        Assert.assertEquals(3, buttons.size());

        int xLocation = buttons.get(0).getLocation().getX();
        for (int i = 1; i < 3; i++) {
            Assert.assertEquals(xLocation, buttons.get(i).getLocation().getX());
        }
    }

    @Test
    public void horizontalLayoutIsRendered() {
        HorizontalLayoutElement horizontalLayoutElement = $(
                HorizontalLayoutElement.class).id("horizontallayout");

        assertElementRendered(horizontalLayoutElement);

        List<ButtonElement> buttons = horizontalLayoutElement
                .$(ButtonElement.class).all();

        Assert.assertEquals(3, buttons.size());

        int yLocation = buttons.get(0).getLocation().getY();
        for (int i = 1; i < 3; i++) {
            Assert.assertEquals(yLocation, buttons.get(i).getLocation().getY());
        }
    }

    @Test
    public void splitLayoutIsRendered() {
        SplitLayoutElement splitLayoutElement = $(SplitLayoutElement.class)
                .id("splithorizontal");

        assertElementRendered(splitLayoutElement);

        TestBenchElement splitter = splitLayoutElement.$(DivElement.class).id("splitter");

        assertElementRendered(splitter);

        List<ButtonElement> labels = splitLayoutElement.$(ButtonElement.class)
                .all();

        Assert.assertEquals(2, labels.size());

        int yLocation = labels.get(0).getLocation().getY();
        Assert.assertEquals(yLocation, labels.get(1).getLocation().getY());
    }

    @Test
    public void menuBarIsRendered() {
        MenuBarElement menuBarElement = $(MenuBarElement.class).id("menubar");

        assertElementRendered(menuBarElement);

        TestBenchElement rootButton = menuBarElement.$("vaadin-menu-bar-button")
                .first();

        assertElementRendered(rootButton);
    }

    @Test
    public void tabsIsRenderedAndRecievesSelectionEvents() {
        TabsElement tabsElement = $(TabsElement.class).first();

        assertElementRendered(tabsElement.$(DivElement.class).id("scroll"));

        List<TabElement> tabs = tabsElement.$(TabElement.class).all();

        Assert.assertEquals(2, tabs.size());

        assertElementRendered(tabs.get(0));

        Assert.assertEquals("foo", tabs.get(0).getText());
        Assert.assertEquals("bar", tabs.get(1).getText());

        getCommandExecutor().executeScript("arguments[0].selected=1",
                tabsElement);

        assertLog("Tabs selected index changed to 1");
    }

    @Test
    public void listBoxIsRenderedAndRecievesValueChangeEvents() {
        TestBenchElement listBoxElement = $("vaadin-list-box").first();

        TestBenchElement itemsContainer = listBoxElement.$(DivElement.class)
                .attribute("part", "items").first();

        assertElementRendered(itemsContainer);

        List<TestBenchElement> items = listBoxElement.$("vaadin-item").all();

        Assert.assertEquals(7, items.size());

        items.stream().forEach(this::assertElementRendered);

        for (int i = 0; i < 7; i++) {
            Assert.assertEquals("Item " + i, items.get(i).getText());
        }

        TestBenchElement listBoxInnerComponent = listBoxElement.$(DivElement.class)
                .id("list-box-component");

        assertElementRendered(listBoxInnerComponent);
        Assert.assertEquals("One more item as a component",
                listBoxInnerComponent.getText());

        getCommandExecutor().executeScript("arguments[0].selected=1",
                listBoxElement);

        assertLog("ListBox value changed from 'null' to 'Item 1'");
    }

    @Test
    public void contextMenuIsRenderedAndRecievesItemSelectionEvents() {

        TestBenchElement contextMenuTarget = $(TestBenchElement.class)
                .id("context-menu-target");

        contextMenuTarget.click();

        // Check to see if the context-menu is there.
        // If not, a NoSuchElementException will be thrown
        $("vaadin-context-menu").first();

        TestBenchElement contextMenuOverlay = $("vaadin-context-menu-overlay").id("overlay");

        assertElementRendered(contextMenuOverlay);

        assertElementRendered(contextMenuOverlay.$(DivElement.class).id("overlay"));

        List<TestBenchElement> items = contextMenuOverlay
                .$("vaadin-context-menu-item").all();
        Assert.assertEquals(2, items.size());

        for (int i = 0; i < 2; i++) {
            assertElementRendered(
                    items.get(0).$(DivElement.class).attribute("part", "content").first());
            Assert.assertEquals("Item " + i, items.get(i).getText());
        }

        getCommandExecutor().executeScript("arguments[0].click();",
                items.get(0));

        assertLog("Context menu Item 0 is clicked");
    }

    @Test
    public void usageStatisticIsLogged() throws InterruptedException {
        Assert.assertTrue($(ButtonElement.class).exists());
        // wait 5 seconds for collecting values in local storage
        Thread.sleep(5000);

        JavascriptExecutor js = (JavascriptExecutor) driver;
        Object mode = js.executeScript("return Vaadin.developmentMode");

        String item = (String) js.executeScript(
                "return window.localStorage.getItem('vaadin.statistics.basket');");

        if (Boolean.TRUE.equals(mode)) {
            Assert.assertTrue(
                    "Under development mode, the checked usage statistics are not found",
                    item.contains("flow") && item.contains("java")
                            && item.contains("vaadin-button"));
        } else {
            Assert.assertTrue(
                    "Under production mode, the usage statistics info should be empty",
                    (item == null || item.length() == 0));
        }

    }

    @BrowserConfiguration
    public List<DesiredCapabilities> getBrowserConfiguration() {
        return Collections
                .singletonList(Browser.CHROME.getDesiredCapabilities());
    }

    private void checkLogsForErrors() {
        getLogEntries(Level.WARNING).forEach(logEntry -> {
            if ((Objects.equals(logEntry.getLevel(), Level.SEVERE)
                    || logEntry.getMessage().contains("404"))) {
                throw new AssertionError(String.format(
                        "Received error message in browser log console right after opening the page, message: %s",
                        logEntry));
            } else {
                LoggerFactory.getLogger(ChromeComponentsIT.class.getName())
                        .warn("This message in browser log console may be a potential error: '{}'",
                                logEntry);
            }
        });
    }

    private List<LogEntry> getLogEntries(Level level) {
        // https://github.com/vaadin/testbench/issues/1233
        getCommandExecutor().waitForVaadin();

        return driver.manage().logs().get(LogType.BROWSER).getAll().stream()
                .filter(logEntry -> logEntry.getLevel().intValue() >= level
                        .intValue())
                // exclude the favicon error
                .filter(logEntry -> !logEntry.getMessage()
                        .contains("favicon.ico"))
                .collect(Collectors.toList());
    }

    private void assertTextComponent(TestBenchElement element,
            String mainhtmlTag, String msg) {
        TestBenchElement input = element.$(mainhtmlTag)
                .attribute("part", "value").first();

        assertElementRendered(input);

        getCommandExecutor().executeScript("arguments[0].value='foo'", element);

        assertLog(msg);
    }

    private void assertLog(String msg) {
        WebElement log = findElement(By.id("log"));
        Assert.assertEquals(msg, log.getText());
    }

    private void assertElementRendered(WebElement element) {
        Assert.assertTrue(element.getSize().getHeight() > 0);
        Assert.assertTrue(element.getSize().getWidth() > 0);
    }

    private File createTempFile() throws IOException {
        File tempFile = File.createTempFile("TestFileUpload", ".txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
        writer.write("foo");
        writer.close();
        tempFile.deleteOnExit();
        return tempFile;
    }

    private void fillPathToUploadInput(String tempFileName) {
        // create a valid path in upload input element. Instead of selecting a
        // file by some file browsing dialog, we use the local path directly.
        WebElement input = $(UploadElement.class).first()
                .$(TestBenchElement.class).id("fileInput");
        setLocalFileDetector(input);
        input.sendKeys(tempFileName);
    }

    private void setLocalFileDetector(WebElement element) {
        if (getRunLocallyBrowser() != null) {
            return;
        }

        if (element instanceof WrapsElement) {
            element = ((WrapsElement) element).getWrappedElement();
        }
        if (element instanceof RemoteWebElement) {
            ((RemoteWebElement) element)
                    .setFileDetector(new LocalFileDetector());
        } else {
            throw new IllegalArgumentException(
                    "Expected argument of type RemoteWebElement, received "
                            + element.getClass().getName());
        }
    }

}
