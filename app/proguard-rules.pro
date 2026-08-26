# Room generates code rather than using reflection, and Compose ships its own
# rules, so the defaults cover almost everything. These two keep the pieces
# that are reached by name rather than by call.

# Entity and DAO names appear in generated SQL and in backup JSON keys.
-keep class org.everyvoice.aac.data.** { *; }

# The backup format is a contract with files on people's phones. Renaming a
# field here would silently break every backup ever exported.
-keep class org.everyvoice.aac.backup.** { *; }
