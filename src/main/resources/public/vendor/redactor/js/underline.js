(function ($)
{
    $.Redactor.prototype.underline = function ()
    {
        return {
            langs: {
                en: {
                    "underline": "Underline"
                }
            },
            init: function ()
            {
                var button = this.button.addAfter('italic', 'underline', this.lang.get('underline'));
                this.button.set('underline', 'S');
                this.button.addCallback(button, this.underline.format);
            },
            format: function ()
            {
                this.inline.format('u');
            }
        };
    };

})(jQuery);