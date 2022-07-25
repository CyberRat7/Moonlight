package net.mehvahdjukaar.moonlight.api.platform.configs.fabric.values;

import net.mehvahdjukaar.moonlight.api.platform.configs.fabric.ConfigEntry;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.function.TriFunction;

import java.util.Objects;
import java.util.function.Supplier;

public abstract class ConfigValue<T> extends ConfigEntry implements Supplier<T> {

    protected final T defaultValue;
    protected T value;
    private String translationKey;
    private String descriptionKey;

    public ConfigValue(String name, T defaultValue){
        super(name);
        this.defaultValue = defaultValue;
        Objects.requireNonNull(defaultValue, "default value cant be null");
        assert this.isValid(defaultValue): "default value is invalid";
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public abstract boolean isValid(T value);

    public void set(T newValue) {
        this.value = newValue;
    }

    @Override
    public T get() {
        return value;
    }

    public void setDescriptionKey(String descriptionKey) {
        this.descriptionKey = descriptionKey;
    }

    public void setTranslationKey(String translationKey) {
        this.translationKey = translationKey;
    }

    public Component getTranslation() {
        return Component.translatable(translationKey);
    }

    public Component getDescription() {
        return Component.translatable(descriptionKey);
    }
}
