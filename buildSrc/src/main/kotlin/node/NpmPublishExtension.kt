package node

import com.google.gson.JsonObject
import org.gradle.api.Action
import java.io.File

open class NpmPublishExtension {

    companion object {
        private val isWindows: Boolean
            get() = File.separatorChar == '\\'

        private val possibleNodePaths: Sequence<String> =
            sequenceOf("node", "bin/node").let { paths ->
                if (isWindows) {
                    paths.map { "$it.exe" } + paths
                } else {
                    paths
                }
            }
    }

    internal var onExtensionChanged: MutableList<NpmPublishExtension.() -> Unit> = mutableListOf()

    private var _nodePath: File = File("")
    var nodePath: File
        get() = _nodePath
        set(value) {
            _nodePath = value
            _node = null
            onExtensionChanged.forEach { it(this) }
        }

    private var _nodeSetupTask: String? = null
    var nodeSetupTask: String?
        get() = _nodeSetupTask
        set(value) {
            _nodeSetupTask = value
            onExtensionChanged.forEach { it(this) }
        }

    private var _jsCompileTask: String? = null
    var jsCompileTask: String?
        get() = _jsCompileTask
        set(value) {
            _jsCompileTask = value
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

    private var _node: File? = null
    internal val node: File
        get() {
            if (_node == null) {
                _node = possibleNodePaths.map { nodePath.resolve(it) }.find { it.exists() }
            }
            return _node ?: File("")
        }

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