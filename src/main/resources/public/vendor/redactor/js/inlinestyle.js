(function($)
{
	$.Redactor.prototype.inlinestyle = function()
	{
		return {
			langs: {
				en: {
					"style": "Style"
				}
			},
			init: function()
			{
				var tags = {
                                        "reded": {
                                            title: "Rojo",
                                            args : ['red']
                                        },
                                        "yellowed": {
                                            title: "Amarillo",
                                            args : ['yellow']
                                        },
                                        "blued": {
                                            title: "Azul",
                                            args : ['blue']
                                        },
                                        "darkblued": {
                                            title: "Azul Oscuro",
                                            args : ['darkblue']
                                        },
                                        "lightblued": {
                                            title: "Celeste",
                                            args : ['lightblue']
                                        },
                                        "greened": {
                                            title: "Verde",
                                            args : ['green']
                                        },
                                        "emeralded": {
                                            title: "Verde Esmeralda",
                                            args : ['emerald']
                                        },
                                        "oranged": {
                                            title: "Naranja",
                                            args : ['orange']
                                        },
                                        "purpled": {
                                            title: "Morado",
                                            args : ['purple']
                                        },
                                        "grayed": {
                                            title: "Gris",
                                            args : ['gray']
                                        },
                                        "darkgrayed": {
                                            title: "Gris Oscuro",
                                            args : ['darkgray']
                                        },
					"marked": {
						title: "Marcado",
						args: ['mark']
					},
					"code": {
						title: "Código",
						args: ['code']
					},
					"sample": {
						title: "Ejemplo",
						args: ['samp']
					},
					"variable": {
						title: "Variable",
						args: ['var']
					},
					"shortcut": {
						title: "Atajo",
						args: ['kbd']
					},
					"cite": {
						title: "Citar",
						args: ['cite']
					},
					"sup": {
						title: "Sobrescrito",
						args: ['sup']
					},
					"sub": {
						title: "Subíndice",
						args: ['sub']
					}
				};


				var that = this;
				var dropdown = {};

				$.each(tags, function(i, s)
				{
					dropdown[i] = { title: s.title, func: 'inline.format', args: s.args };
				});


				var button = this.button.addAfter('format', 'inline', this.lang.get('style'));
				this.button.addDropdown(button, dropdown);

			}


		};
	};
})(jQuery);