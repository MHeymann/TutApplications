# README #

### What is this repository for? ###

This is a program that was developed for data capturing during applications for
the Tshwane University of Technology's community program for improving Matric
maths and science.  The need was identified during previous years when most time
was spent entering data into a spread sheet.  Not only was this time consuming
on the day itself, but in spite of best efforts, many mistakes were still
entered, only to be discovered and corrected later.  

This Program is based on a chat programme that was written for a third year
course at Stellenbosch University.  It thus has a classic client server model.
Clients are used to enter data either, either by the applicants themselves, or
by operators.  The candidate then procedes to the server, where an operator
loads the submited data. It is reviewed there, and then added to a csv file.  

### How do I get set up? ###

The source files are all in the src directory.  

Change to the src directory.
Run the following commands:
	
	$ chmod -v a+x *.sh
	$ ./make.sh

To then run either the client or the server:

	$ ./startserver.sh [OPTIONS]

or

	$ ./startclient.sh [OPTIONS]

as needed. The finalize program is started with 

	$ ./startfinalize.sh

and the validity of id's in packet files in a given directory can be checked
with

	$ ./startcheckid.sh <directory>

or alternatively

	$ ./startcheckid.sh -i <id>

if a specific id number string must be checked.


### General Info ###

The client performs checks on the id number to ensure it is valid and certain
common mistakes weren't made upon typing into the system.  

At every step, a local copy of data is saved, in the 'data' directory by clients
and in 'serverdata' by the server.  The final review program moves entries in
'serverdata' into 'processed' as well as 'csv\_candiates'.  Every time a packet
is sent over the network, a checksum is used to check that data is not
corrupted.  


### Who do I talk to? ###

Murray Heymann

	heymann.murray _at_ gmail _dot_ com
