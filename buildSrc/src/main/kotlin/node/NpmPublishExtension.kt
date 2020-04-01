package node

import com.google.gson.JsonObject
import org.gradle.api.Action
import java.io.File

open class NpmPublishExtension {
    lateinit var nodePath: File
    lateinit var packageJson: File
    lateinit var token: String
    var registry = "registry.npmjs.org"

    private val _liftingActions: MutableList<Action<PackageJson>> = mutableListOf()
    private val _rawLiftingActions: MutableList<Action<JsonObject>> = mutableListOf()

    internal val liftingActions: List<Action<PackageJson>>
        get() = _liftingActions

    internal val rawLiftingActions: List<Action<JsonObject>>
        get() = _rawLiftingActions

    fun liftPackageJson(action: Action<PackageJson>) {
        _liftingActions.add(action)
    }

    fun liftPackageJsonRaw(action: Action<JsonObject>) {
        _rawLiftingActions.add(action)
    }
}