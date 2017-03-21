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


my %DATAVERSE_MAP; 

my $dbh = DBI->connect("DBI:Pg:dbname=$database;host=$host;port=$port",$username,$password); 
my $sth; 

my $query = qq{SELECT ds.authority, ds.identifier, df.filesystemname, df.id, dd.owner_id, fo.createdate, ds.protocol FROM dataset ds, dvobject dd, dvobject fo, datafile df WHERE df.id = fo.id AND fo.owner_id = ds.id AND dd.id = ds.id AND ds.harvestingclient_id IS null ORDER by df.id};

$sth = $dbh->prepare($query); 
$sth->execute();


# this is the header for our tab-separated output file: 

print "FILESYSTEMPATH\tCREATE DATE\tDATASET ID\tDATAVERSE NAME\tCONTACT EMAIL(S)\n";

while ( @_ = $sth->fetchrow() )
{
    my $authority = $_[0];
    my $identifier = $_[1];
    my $filesystemname = $_[2]; 
    my $datafileid = $_[3];
    my $dataverseid = $_[4];
    my $createdate = $_[5];
    my $protocol = $_[6];

    if ($filesystemname) {
	my $filepath = $datadir . "/" . $authority . "/" . $identifier . "/" . $filesystemname;

	unless ( -f $filepath )
	{
	    my $globalid = $protocol . ":" . $authority . "/" . $identifier; 
	    my $dataverseinfo = &find_dataverse_info($dataverseid); 

	    print $filepath . "\t" . $createdate . "\t" . $globalid . "\t" . $dataverseinfo . "\n";
	}
    }
    else 
    {
	print STDERR "datafile (id = $datafileid) is missing the filesystemname!\n";
    }

}
$sth->finish; 
$dbh->disconnect; 

exit 0; 

  

sub find_dataverse_info {
    my $dataverseid = $_[0]; 

    # we look up AND CACHE the dataverse names + contact email(s) - 
    # since many of the files will be from the same datasets: 

    if ($DATAVERSE_MAP{$dataverseid}) 
    {
	return $DATAVERSE_MAP{$dataverseid};
    }

    my $q1 = qq{SELECT name FROM dataverse WHERE id=$dataverseid};

    my $sth1 = $dbh->prepare($q1); 
    $sth1->execute();
    @_ = $sth1->fetchrow();
    my $dvname = $_[0]; 
    $sth1->finish; 

    $q1 = qq{SELECT contactemail FROM dataversecontact WHERE dataverse_id=$dataverseid};

    $sth1 = $dbh->prepare($q1); 
    $sth1->execute();
    my @emails = ();
    while (@_ = $sth1->fetchrow()) 
    {
	push(@emails, $_[0]);
	
    }
    $sth1->finish;


    $DATAVERSE_MAP{$dataverseid} = $dvname . "\t" . join (", ", @emails);

    return $DATAVERSE_MAP{$dataverseid};
}
