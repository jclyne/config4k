package io.github.config4k.readers

import com.typesafe.config.Config
import com.typesafe.config.ConfigValue
import io.github.config4k.ClassContainer
import io.github.config4k.Config4kException
import java.time.Duration
import kotlin.reflect.full.primaryConstructor


object SelectReader {
    /**
     * Don't use this method.
     * Called by an inline function [io.github.config4k.Extension.extract],
     * this method is public even though it is just for internal.
     *
     * Add new case to support new type.
     *
     * @param clazz a instance got from the given type by reflection
     * @throws Config4kException.UnSupportedType if the passed type is not supported
     */
    fun getReader(clazz: ClassContainer) =
            when (clazz.mapperClass) {
                Int::class -> IntReader()
                String::class -> StringReader()
                Boolean::class -> BooleanReader()
                Double::class -> DoubleReader()
                Long::class -> LongReader()
                Duration::class -> DurationReader()
                Config::class -> ConfigReader()
                ConfigValue::class -> ConfigValueReader()
                List::class -> ListReader(clazz.typeArguments)
                Set::class -> SetReader(clazz.typeArguments)
                Map::class -> MapReader(clazz.typeArguments)
                else ->
                    when {
                        clazz.mapperClass.java.isArray ->
                            ArrayReader(clazz.mapperClass.java.componentType.kotlin)
                        clazz.mapperClass.java.isEnum -> EnumReader(clazz.mapperClass)
                        clazz.mapperClass.primaryConstructor != null -> ArbitraryTypeReader(clazz)
                        clazz.mapperClass.objectInstance != null -> ObjectReader(clazz)
                        else -> throw Config4kException.UnSupportedType(clazz.mapperClass)
                    }
            }.getValue
}
