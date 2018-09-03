define(['color'], function(Color) {
    describe("visualization/color", function() {
        var color;

        var greenHex   = '#00ff00';
        var greenRGB   = [0, 255, 0];
        var blueHex    = '#0000ff';
        var blueRGB    = [0, 0, 255];
        var brokenHex1 = '+0z0000';
        var brokenHex2 = '#0';
        var brokenRGB1 = [-2, 0, 300];
        var brokenRGB2 = [0, 3];

        describe("constructor", function() {
            it("instantiate with hex color", function() {
                color = new Color(greenHex);
                expect(color.getHex()).toEqual(greenHex);
                expect(color.getRGB()).toEqual(greenRGB);
            });

            it("instantiate with rgb color", function() {
                color = new Color(greenRGB);
                expect(color.getHex()).toEqual(greenHex);
                expect(color.getRGB()).toEqual(greenRGB);
            });
        });

        describe("setters and getters", function() {
            beforeEach(function() {
                color = new Color(greenHex);
            });

            it("set and get valid hex color", function() {
                color.setHex(blueHex);
                expect(color.getHex()).toEqual(blueHex);
            });

            it("set invalid hex color", function() {
                expect(function() {
                    color.setHex(brokenHex1)
                }).toThrow();
                expect(function() {
                    color.setHex(brokenHex2)
                }).toThrow();
            });

            it("set and get valid rgb color", function() {
                color.setRGB(blueRGB);
                expect(color.getRGB()).toEqual(blueRGB);
            });

            it("set invalid rgb color", function() {
                expect(function() {
                    color.setRGB(brokenRGB1)
                }).toThrow();
                expect(function() {
                    color.setRGB(brokenRGB2)
                }).toThrow();
            });
        });
    });
});