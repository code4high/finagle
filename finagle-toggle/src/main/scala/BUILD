scala_library(
  provides = scala_artifact(
    org = 'com.twitter',
    name = 'finagle-toggle',
    repo = artifactory,
  ),
  dependencies=[
    '3rdparty/jvm/com/fasterxml/jackson/core:jackson-annotations',
    '3rdparty/jvm/com/fasterxml/jackson/core:jackson-databind',
    '3rdparty/jvm/com/fasterxml/jackson/module:jackson-module-scala',
    'util/util-app/src/main/java',
    'util/util-app/src/main/scala',
    'util/util-logging',
    'util/util-registry',
    'util/util-stats',
    'util/util-core/src/main/scala:scala',
  ],
  fatal_warnings=True,
  strict_deps=True,
  exports=[
    '3rdparty/jvm/com/fasterxml/jackson/core:jackson-annotations',
  ],
  sources=rglobs('*.scala'),
)
