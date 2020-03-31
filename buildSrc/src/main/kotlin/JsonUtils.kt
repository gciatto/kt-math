import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.io.File
import java.io.FileReader
import java.io.FileWriter

fun <R> File.updateJsonFile(f: JsonObject.() -> R): R {
    if (!exists()) {
        error("File $path does not exist")
    }
    val gson = GsonBuilder().setPrettyPrinting().create()
    val obj = gson.fromJson(FileReader(this), JsonObject::class.java)
    val newObject = obj.deepCopy()
    val result = newObject.f()
    if (obj != newObject) {
        FileWriter(this).use {
            gson.toJson(newObject, it)
        }
    }
    return result
}

fun jsonObject(vararg keys: Pair<String, String>): JsonObject {
    val obj = JsonObject()
    for (kv in keys) {
        obj.setKey(kv.first, kv.second)
    }
    return obj
}

fun JsonObject.getKey(key: String): String {
    return get(key).asString
}

fun JsonObject.setKey(key: String, value: String) {
    addProperty(key, value)
}

fun JsonObject.setKey(key: String, value: JsonElement) {
    add(key, value)
}

fun JsonObject.setKey(key: String, vararg keyValues: Pair<String, String>) {
    add(key, jsonObject(*keyValues))
}

fun JsonObject.updateKey(key: String, f: (String) -> String) {
    setKey(key, f(getKey(key)))
}