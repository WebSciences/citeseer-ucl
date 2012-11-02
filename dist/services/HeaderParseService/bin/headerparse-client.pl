#!/opt/ActivePerl-5.8/bin/perl -CSD
#
# Copyright 2007 Penn State University
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#     http://www.apache.org/licenses/LICENSE-2.0
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
#
# Simple SOAP client for the SVMHeaderParse web service.
#
use FindBin;
use SOAP::Lite +trace=>'debug';
use MIME::Base64;

my $filePath = $ARGV[0];
my $repositoryID = $ARGV[1];

if (!defined $filePath || !defined $repositoryID) {
    print "Usage: $0 textFile repositoryID\n".
	"Specify \"LOCAL\" as repository if using local file system\n";
    exit;
}

my $HeaderParseService = SOAP::Lite
    ->service("file:$FindBin::Bin/../wsdl/SVMHeaderParse.wsdl")
    ->on_fault(sub {
	my($soap, $res) = @_;
	die ref $res ? $res->faultstring : $soap->transport->status;
    });

my $response = $HeaderParseService->parseHeader($filePath, $repositoryID);
print $response;


