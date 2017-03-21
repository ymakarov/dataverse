#!/usr/bin/perl

use DBI;

my $datadir = "/opt/dvn/data/IQSS/DVN/data";

# EDIT the configuration values below to 
# work with your database:
my $port = 5432;
my $host = "XXXX"; 
my $username = "dvnapp";
my $password = "xxx";
my $database = "dvndb";


my $dbh = DBI->connect("DBI:Pg:dbname=$database;host=$host;port=$port",$username,$password); 
my $sth; 


my $query = qq{SELECT ds.authority, ds.identifier, df.filesystemname, df.id FROM dataset ds, dvobject fo, datafile df WHERE df.id = fo.id AND fo.owner_id = ds.id AND ds.harvestingclient_id IS null AND df.filesize = 0 ORDER by df.id};

$sth = $dbh->prepare($query); 
$sth->execute();


while ( @_ = $sth->fetchrow() )
{
    my $authority = $_[0];
    my $identifier = $_[1];
    my $filesystemname = $_[2]; 
    my $datafileid = $_[3];

    if ($filesystemname) {
    my $filepath = $datadir . "/" . $authority . "/" . $identifier . "/" . $filesystemname;

    my $filesize = (stat($filepath))[7]; 

    if ($filesize) 
    {
	#print $filepath . "\n";
	print "UPDATE datafile SET filesize=$filesize WHERE id=$datafileid;\n";
    }
    else 
    {
	print STDERR "$filepath (id = $datafileid) is missing.\n";
    }
    }
    else 
    {
	print STDERR "datafile (id = $datafileid) is missing the filesystemname.\n";
    }

}
$sth->finish; 

$dbh->disconnect; 

exit 0; 

  
