#!/usr/bin/perl

use DBI;

# EDIT the configuration values below to                                                                                        
# match your system:
my $datadir = "/opt/dvn/data/IQSS/DVN/data";
# database access: 
my $port = 5432;
my $host = "XXXX";
my $username = "dvnapp";
my $password = "xxx";
my $database = "dvndb";


my $verbose = 0; 

while (my $opt = shift @ARGV) 
{
    $verbose = 1 if ($opt =~/^\-+v/); 
}
    

my %DATAVERSE_MAP; 

my $dbh = DBI->connect("DBI:Pg:dbname=$database;host=$host;port=$port",$username,$password); 
my $sth; 

my $query = qq{SELECT ds.authority, ds.identifier, df.filesystemname, df.id, dd.owner_id, fo.createdate, ds.protocol, df.filesize, df.checksumvalue FROM dataset ds, dvobject dd, dvobject fo, datafile df WHERE df.id = fo.id AND fo.owner_id = ds.id AND dd.id = ds.id AND ds.harvestingclient_id IS null ORDER by df.id};

$sth = $dbh->prepare($query); 
$sth->execute();


print "FILE ID\tFILESYSTEMPATH\t" if $verbose; 
print "FILE NAME\tCREATE DATE\tSIZE\tMD5\tDATASET ID\tDATAVERSE NAME\tCONTACT EMAIL(S)\n";

while ( @_ = $sth->fetchrow() )
{
    my $authority = $_[0];
    my $identifier = $_[1];
    my $filesystemname = $_[2]; 
    my $datafileid = $_[3];
    my $dataverseid = $_[4];
    my $createdate = $_[5];
    my $protocol = $_[6];
    my $filesize = $_[7];
    my $md5 = $_[8]; 

    my $filepath = $datadir . "/" . $authority . "/" . $identifier . "/" . $filesystemname;

    unless ($filesystemname && -f $filepath )
    {
	my $globalid = $protocol . ":" . $authority . "/" . $identifier; 

	my $dataverseinfo = &find_dataverse_info($dataverseid); 
	my $filelabel = &get_filelabel($datafileid);

	print $datafileid . "\t" . $filepath . "\t" if $verbose; 
	print $filelabel . "\t" . $createdate . "\t" . $filesize . "\t" . $md5 . "\t" . $globalid . "\t" . $dataverseinfo . "\n";
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

sub get_filelabel {
    my $datafileid = $_[0]; 

    my $q2 = qq{SELECT label FROM filemetadata WHERE datafile_id=$datafileid ORDER BY id DESC LIMIT 1};

    my $sth2 = $dbh->prepare($q2); 
    $sth2->execute();
    my $filelabel;
    if (@_ = $sth2->fetchrow())
    {
	$filelabel =  $_[0];
    } 
    else 
    {
	print STDERR "no filemetadatas for file id $datafileid.\n";
	$filelabel = undef; 
    }
    
    $sth2->finish; 
    return $filelabel;
} 
