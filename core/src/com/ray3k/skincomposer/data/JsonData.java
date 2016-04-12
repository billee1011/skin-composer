package com.ray3k.skincomposer.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton.ImageTextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin.TintedDrawable;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane.SplitPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip.TextTooltipStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.TreeStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import java.io.StringWriter;

public class JsonData implements Json.Serializable {
    private static JsonData instance;
    private static Array<Class> styleClasses;
    private Array<ColorData> colors;
    private Array<FontData> fonts;
    private OrderedMap<Class, Array<StyleData>> classStyleMap;
    
    public static JsonData getInstance() {
        if (instance == null) {
            instance = new JsonData();
        }
        return instance;
    }
    
    public static void loadInstance(JsonData instance) {
        JsonData.instance = instance;
    }
    
    private JsonData() {
        instance = this;
        styleClasses = new Array<Class>(new Class[] {ButtonStyle.class, CheckBoxStyle.class,
            ImageButtonStyle.class, ImageTextButtonStyle.class, LabelStyle.class, ListStyle.class,
            ProgressBarStyle.class, ScrollPaneStyle.class, SelectBoxStyle.class, SliderStyle.class,
            SplitPaneStyle.class, TextButtonStyle.class, TextFieldStyle.class, TextTooltipStyle.class,
            TouchpadStyle.class, TreeStyle.class, WindowStyle.class});
        colors = new Array<ColorData>();
        fonts = new Array<FontData>();
        
        initializeClassStyleMap();
    }
    
    public void clear() {
        colors.clear();
        fonts.clear();
        initializeClassStyleMap();
    }
    
    public void readFile(FileHandle fileHandle) {
        JsonReader reader = new JsonReader();
        JsonValue val = reader.parse(fileHandle);
        System.out.println(val.toJson(OutputType.minimal));
    }
    
    public void writeFile(FileHandle fileHandle) {
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);
        jsonWriter.setOutputType(OutputType.minimal);
        Json json = new Json();
        json.setWriter(jsonWriter);
        json.writeObjectStart();
        
        //fonts
        if (fonts.size > 0) {
            json.writeObjectStart(BitmapFont.class.getName());
                for (FontData font : fonts) {
                    json.writeObjectStart(font.getName());
                    json.writeValue("file", font.file.name());
                    json.writeObjectEnd();
                }
            json.writeObjectEnd();
        }
        
        //colors
        if (colors.size > 0) {
            json.writeObjectStart(Color.class.getName());
                for (ColorData color : colors) {
                    json.writeObjectStart(color.getName());
                    json.writeValue("r", color.color.r);
                    json.writeValue("g", color.color.g);
                    json.writeValue("b", color.color.b);
                    json.writeValue("a", color.color.a);
                    json.writeObjectEnd();
                }
            json.writeObjectEnd();
        }
        
        //tinted drawables
        Array<DrawableData> tintedDrawables = new Array<>();
        for (DrawableData drawable : AtlasData.getInstance().getDrawables()) {
            if (drawable.tint != null) {
                tintedDrawables.add(drawable);
            }
        }
        if (tintedDrawables.size > 0) {
            json.writeObjectStart(TintedDrawable.class.getName());
                for (DrawableData drawable : tintedDrawables) {
                    json.writeObjectStart(drawable.name);
                    json.writeValue("name", DrawableData.proper(drawable.file.name()));
                    json.writeObjectStart("color");
                    json.writeValue("r", drawable.tint.r);
                    json.writeValue("g", drawable.tint.g);
                    json.writeValue("b", drawable.tint.b);
                    json.writeValue("a", drawable.tint.a);
                    json.writeObjectEnd();
                    json.writeObjectEnd();
                }
            json.writeObjectEnd();
        }
        
        //styles
        Array<Array<StyleData>> valuesArray = classStyleMap.values().toArray();
        for (int i = 0; i < styleClasses.size; i++) {
            Class clazz = styleClasses.get(i);
            Array<StyleData> styles = valuesArray.get(i);
            
            //check if any style has the mandatory fields necessary to write
            boolean hasMandatoryStyles = true;
            for (StyleData style : styles) {
                if (!style.hasMandatoryFields() || style.hasAllNullFields()) {
                    hasMandatoryStyles = false;
                    break;
                }
            }
            
            if (hasMandatoryStyles) {
                json.writeObjectStart(clazz.getName());
                for (StyleData style : styles) {
                    if (style.hasMandatoryFields() && !style.hasAllNullFields()) {
                        json.writeObjectStart(style.name);
                        for (StyleProperty property : style.properties.values()) {

                            //if not optional, null, or zero
                            if (!property.optional || property.value != null
                                    && !(property.value instanceof Number
                                    && MathUtils.isZero((float) (double) property.value)))
                            json.writeValue(property.name, property.value);
                        }
                        json.writeObjectEnd();
                    }
                }
                json.writeObjectEnd();
            }
        }
        
        json.writeObjectEnd();
        fileHandle.writeString(json.prettyPrint(stringWriter.toString()), false);
    }

    public Array<ColorData> getColors() {
        return colors;
    }

    public Array<FontData> getFonts() {
        return fonts;
    }

    public OrderedMap<Class, Array<StyleData>> getClassStyleMap() {
        return classStyleMap;
    }
    
    private void initializeClassStyleMap() {
        classStyleMap = new OrderedMap();
        for (Class clazz : StyleData.classes) {
            Array<StyleData> array = new Array<StyleData>();
            classStyleMap.put(clazz, array);
            if (clazz.equals(Slider.class) || clazz.equals(ProgressBar.class) || clazz.equals(SplitPane.class)) {
                StyleData data = new StyleData(clazz, "default-horizontal");
                data.deletable = false;
                array.add(data);
                data = new StyleData(clazz, "default-vertical");
                data.deletable = false;
                array.add(data);
            } else {
                StyleData data = new StyleData(clazz, "default");
                data.deletable = false;
                array.add(data);
            }
        }
    }

    @Override
    public void write(Json json) {
        json.writeValue("colors", colors);
        json.writeValue("fonts", fonts);
        json.writeValue("classStyleMap", classStyleMap);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        try {
            colors = json.readValue("colors", Array.class, jsonData);
            fonts = json.readValue("fonts", Array.class, jsonData);
            classStyleMap = new OrderedMap<>();
            for (JsonValue data : jsonData.get("classStyleMap").iterator()) {
                classStyleMap.put(ClassReflection.forName(data.name), json.readValue(Array.class, data));
            }
        } catch (ReflectionException e) {
            Gdx.app.log(getClass().getName(), "Error parsing json data during file read", e);
        }
    }
}
