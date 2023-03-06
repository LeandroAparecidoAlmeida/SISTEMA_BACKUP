program HDDSerialNumber;

{$APPTYPE CONSOLE}

uses
  Windows,
  SysUtils,
  hddinfo in 'hddinfo.pas';

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
