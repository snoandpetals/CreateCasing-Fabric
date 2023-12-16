package fr.iglee42.createcasing.compat.kubejs;

import dev.latvian.mods.kubejs.client.LangEventJS;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.kubejs.generator.DataJsonGenerator;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.util.HideFromJS;

import java.util.ArrayList;
import java.util.List;

public abstract class CreateCasingBuilderBaseJS {

    public static List<CreateCasingBuilderBaseJS> BUILDERS = new ArrayList<>();

    public final String name;
    public final String fullName;

    public String translationKey;
    private final String translateCategory;

    public String displayName;

    public CreateCasingBuilderBaseJS(String name,String fullName,String translateCategory) {
        this.name = name;
        this.fullName = fullName;
        this.translateCategory = translateCategory;
        this.displayName = "";
        this.translationKey = "";
        BUILDERS.add(this);
    }

    @HideFromJS
    public void generateDataJsons(DataJsonGenerator generator) {
    }

    @HideFromJS
    public void generateAssetJsons(AssetJsonGenerator generator) {
    }

    @HideFromJS
    public String getBuilderTranslationKey() {
        if (translationKey.isEmpty()) {
            return translateCategory + ".createcasing-kubejs." + fullName;
        }

        return translationKey;
    }

    @HideFromJS
    public void generateLang(LangEventJS lang) {
        var dname = displayName;

        if (dname.isEmpty()) {
            dname = UtilsJS.snakeCaseToTitleCase(fullName);
        }

        lang.add("createcasing-kubejs", getBuilderTranslationKey(), dname);
    }




}
