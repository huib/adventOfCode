// https://stackoverflow.com/a/53018129/2065212
fun getInput(path: String): String =
    object {}.javaClass.getResource(path)?.readText() ?: error("file not found: $path")
