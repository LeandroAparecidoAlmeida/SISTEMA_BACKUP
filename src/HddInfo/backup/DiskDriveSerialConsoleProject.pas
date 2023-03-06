program DiskDriveSerialConsoleProject;

{$APPTYPE CONSOLE}

uses
  Windows,
  SysUtils,
  hddinfo in 'hddinfo.pas';

const
  // Max number of drives assuming primary/secondary, master/slave topology
  MAX_IDE_DRIVES = 16;

procedure ReadPhysicalDriveInNTWithZeroRights ();
var
  DriveNumber: Byte;
  HDDInfo: THDDInfo;
begin
  HDDInfo := THDDInfo.Create();
  try
    for DriveNumber := 0 to MAX_IDE_DRIVES - 1 do
    try
      HDDInfo.DriveNumber := DriveNumber;
      if HDDInfo.IsInfoAvailable then
      begin
        Writeln('VendorId: ', HDDInfo.VendorId);
        Writeln('ProductId: ', HDDInfo.ProductId);
        Writeln('ProductRevision: ', HDDInfo.ProductRevision);
        Writeln('SerialNumber: ', HDDInfo.SerialNumber);
        Writeln('SerialNumberInt: ', HDDInfo.SerialNumberInt);
        Writeln('SerialNumberText: ', HDDInfo.SerialNumberText);
      end;
    except
      on E: Exception do
        Writeln(Format('DriveNumber %d, %s: %s', [DriveNumber, E.ClassName, E.Message]));
    end;
  finally
    HDDInfo.Free;
  end;
end;

begin
  ReadPhysicalDriveInNTWithZeroRights;
  Write('Press <Enter>');
  Readln;
end.