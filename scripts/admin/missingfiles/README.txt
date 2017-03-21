The script_find_missing_files.pl will go through all the local
(non-harvested) files in the database, and check if the physical file
is still on the file system.

It will output all the missing files found in a tab-delimited format
(that could be used to create a spreadsheet), as follows:

FILESYSTEMPATH 	    CREATE DATE	   DATASET ID    DATAVERSE NAME	   CONTACT EMAIL(S)

("DATASET ID" is the global id of the dataset). 

Before you can run it, you must edit the top portion of the script
with your Postgres database access credentials.



The script script_check_missing_sizes.pl will look for any datafiles
in the database that do not have the file size set, and if the
physical file is actually present, check its size, and output the SQL
commands needed to fix the size in the database. (Rather than just
fixing it in the database, it will give the admin a chance to first
review the output...)

The whole process will be something like (for example)

./script_find_missing_files.pl > restoresizes.sql
psql -d dvndb -U dvnapp -f restoresizes.sql

Similarly to the other script, you will need to edit it and configure 
the database access credentials. 


