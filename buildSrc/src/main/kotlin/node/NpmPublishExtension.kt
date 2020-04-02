package node

import com.google.gson.JsonObject
import org.gradle.api.Action
import java.io.File

open class NpmPublishExtension {

    internal var onExtensionChanged: MutableList<NpmPublishExtension.() -> Unit> = mutableListOf()

    private var _nodePath: File = File("")
    var nodePath: File
        get() = _nodePath
        set(value) {
            _nodePath = value
            onExtensionChanged.forEach { it(this) }
        }

    private var _packageJson: File = File("")
    var packageJson: File
        get() = _packageJson
        set(value) {
            _packageJson = value
            onExtensionChanged.forEach { it(this) }
        }

    private var _token: String = ""
    var token: String
        get() = _token
        set(value) {
            _token = value
            onExtensionChanged.forEach { it(this) }
        }

    var registry = "registry.npmjs.org"
        get() = field
        set(value) {
            field = value
            onExtensionChanged.forEach { it(this) }
        }

    internal val node: File
        get() = nodePath.resolve("node")

    internal val npm: File
        get() = nodePath.resolve("node_modules/npm/bin/npm-cli.js")

    internal val npmProject: File
        get() = File(packageJson.parent)

    private val _liftingActions: MutableList<Action<PackageJson>> = mutableListOf()
    private val _rawLiftingActions: MutableList<Action<JsonObject>> = mutableListOf()

    internal val liftingActions: List<Action<PackageJson>>
        get() = _liftingActions.toList()

    internal val rawLiftingActions: List<Action<JsonObject>>
        get() = _rawLiftingActions.toList()

    fun liftPackageJson(action: Action<PackageJson>) {
        _liftingActions.add(action)
        onExtensionChanged.forEach { it(this) }
    }

    fun liftPackageJsonRaw(action: Action<JsonObject>) {
        _rawLiftingActions.add(action)
        onExtensionChanged.forEach { it(this) }
    }
}