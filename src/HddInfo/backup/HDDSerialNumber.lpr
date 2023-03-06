program HDDSerialNumber;

{$APPTYPE CONSOLE}

uses
  Windows,
  SysUtils,
  hddinfo in 'hddinfo.pas';

{procedure ReadPhysicalDriveInNTWithZeroRights ();
var
  HDDInfo: THDDInfo;
begin
     HDDInfo := THDDInfo.Create();
     try
        Writeln(Trim(HDDInfo.SerialNumber));
     finally
       HDDInfo.Free;
     end;
end;    }

var
  HDDInformation: THDDInfo;

begin
     HDDInformation := THDDInfo.Create();
     try
        Writeln(Trim(HDDInformation.SerialNumber));
     finally
       HDDInformation.Free;
     end;
end.
