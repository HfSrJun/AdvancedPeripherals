local p = peripheral.find("geoScanner")
test.assert(p, "There is no scanner")
local result, err = p.scan(3)
test.assert(result, "There is no scan result")
test.eq(nil, err, "Err should be nil")