program Partitions;

{$mode objfpc}{$H+}
uses
  SysUtils, ActiveX, ComObj, variants, Windows, ctypes, types;

type
  TStringArray = array of string;

var
   logicalDisks : TStringArray = NIL;
   phisicalDisks : TStringArray = NIL;
   drivesList : TStringArray = NIL;
   FSWbemLocator : Variant;
   objWMIService : Variant;
   colDiskDrivesWin32DiskDrive : Variant;
   colLogicalDisks : Variant;
   colPartitions : Variant;
   objLogicalDisk : OLEVariant;
   objdiskDrive : OLEVariant;
   objPartition : OLEVariant;
   oEnumDiskDrive : IEnumvariant;
   oEnumPartition : IEnumvariant;
   oEnumLogical : IEnumvariant;
   nrValue : LongWord;
   nr : LongWord absolute nrValue;
   size : Integer = 0;
   idx : Integer;
   val1 : WideString;
   val2 : WideString;
   deviceID : WideString;
   query : WideString;
   windowsDrive : string = '';
   windowsPath : String = '';
   windowsDisk : String = '';

begin
   try
      FSWbemLocator := CreateOleObject('WbemScripting.SWbemLocator');
      objWMIService := FSWbemLocator.ConnectServer('localhost', 'root\CIMV2', '', '');
      colDiskDrivesWin32DiskDrive := objWMIService.ExecQuery('SELECT * FROM Win32_DiskDrive', 'WQL');
      oEnumDiskDrive := IUnknown(colDiskDrivesWin32DiskDrive._NewEnum) as IEnumVariant;
      while oEnumDiskDrive.Next(1, objdiskDrive, nr) = 0 do
      begin
         deviceID := WideString(StringReplace(objdiskDrive.DeviceID, '\', '\\', [rfReplaceAll]));
         query := WideString(Format('ASSOCIATORS OF {Win32_DiskDrive.DeviceID="%s"} WHERE AssocClass = Win32_DiskDriveToDiskPartition', [deviceID]));
         colPartitions := objWMIService.ExecQuery(query, 'WQL');
         oEnumPartition := IUnknown(colPartitions._NewEnum) as IEnumVariant;
         while oEnumPartition.Next(1, objPartition, nr) = 0 do
         begin
            if not VarIsNull(objPartition.DeviceID) then
            begin
               query := 'ASSOCIATORS OF {Win32_DiskPartition.DeviceID="' + WideString(VarToStr(objPartition.DeviceID)) + '"} WHERE AssocClass = Win32_LogicalDiskToPartition';
               colLogicalDisks := objWMIService.ExecQuery(query);
               oEnumLogical := IUnknown(colLogicalDisks._NewEnum) as IEnumVariant;
               while oEnumLogical.Next(1, objLogicalDisk, nr) = 0 do
               begin
                  val1 := WideString(Format('%s', [objLogicalDisk.DeviceID]));
                  val2 := WideString(Format('%s', [objPartition.DeviceID]));
                  if Length(val1) > 0 then
                  begin
                     Inc(size);
                     SetLength(logicalDisks, size);
                     SetLength(phisicalDisks, size);
                     logicalDisks[size - 1] := String(val1);
                     phisicalDisks[size - 1] := String(val2).Split(',')[0];
                  end;
                  objLogicalDisk := Unassigned;
               end;
            end;
            objPartition := Unassigned;
         end;
         objdiskDrive := Unassigned;
      end;
      SetLength(windowsPath, MAX_PATH);
      if GetWindowsDirectory(PChar(windowsPath), MAX_PATH) > 0 then
      begin
         windowsDrive := windowsPath.Substring(0, 2);
      end;
      for idx := Low(logicalDisks) to High(logicalDisks) do
      begin
         if String.Compare(logicalDisks[idx], windowsDrive) = 0 then
         begin
            windowsDisk := phisicalDisks[idx];
            Break;
         end;
      end;
      size := 0;
      for idx := Low(logicalDisks) to High(logicalDisks) do
      begin
         if String.Compare(phisicalDisks[idx], windowsDisk) = 0 then
         begin
            Inc(size);
            SetLength(drivesList, size);
            drivesList[size - 1] := logicalDisks[idx];
         end;
      end;
      Write(drivesList[0]);
      for idx := 1 to High(drivesList) do
      begin
         WriteLn();
         Write(drivesList[idx]);
      end;
   except
      on E:EOleException do Writeln('');
      on E:Exception do Writeln('');
   end;
end.
