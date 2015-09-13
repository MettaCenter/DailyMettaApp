Reviewed by:


### Risks

### Defects

Defect | Area | Notes
--- | --- | ---
Searchable activity called twice on the emulator | - | This seems to only be a problem for the emulator: http://stackoverflow.com/questions/15633282/searchable-activity-being-called-twice. Please note that we seem to be getting this error at the same time: ```finishComposingText on inactive InputConnection```
Logcat has a limitation in that it shows only 4096 characters which will cut off data from the atom feed | 

