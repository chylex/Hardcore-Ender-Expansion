@xcopy "eclipse/.metadata" "eclipse/.metadata.back" /E /H /I /Q
@start /B gradlew.bat setupDecompWorkspace eclipse
@rmdir /S /Q "eclipse/.metadata"
@ren "eclipse/.metadata.back" ".metadata"