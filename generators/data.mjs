export const nuggets = [
    {
        "name": "native_copper",
        "result": "copper",
        "min_temp": 864+87+87
    },
    {
        "name": "native_gold",
        "result": "gold",
        "min_temp": 848+84+84
    },
    {
        "name": "hematite",
        "result": "cast_iron",
        "min_temp": 1228+122+122
    },
    {
        "name": "native_silver",
        "result": "silver",
        "min_temp": 769+79+79
    },
    {
        "name": "cassiterite",
        "result": "tin",
        "min_temp": 184+18+18
    },
    {
        "name": "bismuthinite",
        "result": "bismuth",
        "min_temp": 216+21+21
    },
    {
        "name": "garnierite",
        "result": "nickel",
        "min_temp": 1162+116+116
    },
    {
        "name": "malachite",
        "result": "copper",
        "min_temp": 864+87+87
    },
    {
        "name": "magnetite",
        "result": "cast_iron",
        "min_temp": 1228+122+122
    },
    {
        "name": "limonite",
        "result": "cast_iron",
        "min_temp": 1228+122+122
    },
    {
        "name": "sphalerite",
        "result": "zinc",
        "min_temp": 336+33+33
    },
    {
        "name": "tetrahedrite",
        "result": "copper",
        "min_temp": 864+86+86
    }
];

export const moldable_metals = [
    "bismuth_bronze",
    "black_bronze",
    "bronze",
    "copper",
];

export const moldables = [
    {
        name: "scythe_blade",
        unit: 100
    },
    {
        name: "mace_head",
        unit: 200
    },
    {
        name: "knife_blade",
        unit: 100
    },
    {
        name: "sword_blade",
        unit: 200
    },
    {
        name: "pickaxe_head",
        unit: 100
    },
    {
        name: "propick_head",
        unit: 100
    },
    {
        name: "axe_head",
        unit: 100
    },
    {
        name: "shovel_head",
        unit: 100
    },
    {
        name: "hoe_head",
        unit: 100
    },
    {
        name: "chisel_head",
        unit: 100
    },
    {
        name: "hammer_head",
        unit: 100
    },
    {
        name: "saw_blade",
        unit: 100
    },
    {
        name: "javelin_head",
        unit: 100
    }
]

export const metals = [
    {
        name: "bismuth",
        min_temp: 270 //216+21+21
    },
    {
        name: "bismuth_bronze",
        min_temp: 788+78+78
    },
    {
        name: "black_bronze",
        min_temp: 856+85+85
    },
    {
        name: "bronze",
        min_temp: 760+76+76
    },
    {
        name: "brass",
        min_temp: 744+74+74
    },
    {
        name: "copper",
        min_temp: 864+85+85
    },
    {
        name: "gold",
        min_temp: 848+84+84
    },
    {
        name: "nickel",
        min_temp: 1162+116+116
    },
    {
        name: "rose_gold",
        min_temp: 768+76+76
    },
    {
        name: "silver",
        min_temp: 769+76+76
    },
    {
        name: "tin",
        min_temp: 184+18+18
    },
    {
        name: "zinc",
        min_temp: 336+33+33
    },
    {
        name: "sterling_silver",
        min_temp: 760+76+76
    },
    {
        name: "wrought_iron",
        min_temp: 1228+122+122
    },
    {
        name: "cast_iron",
        min_temp: 1228+122+122
    },
    {
        name: "pig_iron",
        min_temp: 1228+122+122
    },
    {
        name: "steel",
        min_temp: 1232+123+123
    },
    {
        name: "black_steel",
        min_temp: 1188+118+118
    },
    {
        name: "blue_steel",
        min_temp: 1232+123+123
    },
    {
        name: "red_steel",
        min_temp: 1232+123+123
    },
    {
        name: "weak_steel",
        min_temp: 1232+123+123
    },
    {
        name: "weak_blue_steel",
        min_temp: 1232+123+123
    },
    {
        name: "weak_red_steel",
        min_temp: 1232+123+123
    },
    {
        name: "high_carbon_steel",
        min_temp: 1232+123+123
    },
    {
        name: "high_carbon_black_steel",
        min_temp: 1232+123+123
    },
    {
        name: "high_carbon_blue_steel",
        min_temp: 1232+123+123
    },
    {
        name: "high_carbon_red_steel",
        min_temp: 1232+123+123
    },
    {
        name: "unknown",
        min_temp: 320+32+32
    }
];

export const metal_temps = {
    "bismuth": 270,
    "bismuth_bronze": 985,
    "black_bronze": 1070,
    "bronze": 950,
    "brass": 930,
    "copper": 1080,
    "gold": 1060,
    "nickel": 1453,
    "rose_gold": 960,
    "silver": 961,
    "tin": 230,
    "zinc": 420,
    "sterling_silver": 950,
    "wrought_iron": 1535,
    "cast_iron": 1535,
    "pig_iron": 1535,
    "steel": 1540,
    "black_steel": 1485,
    "blue_steel": 1540,
    "red_steel": 1540,
    "weak_steel": 1540,
    "weak_blue_steel": 1540,
    "weak_red_steel": 1540,
    "high_carbon_steel": 1540,
    "high_carbon_black_steel": 1540,
    "high_carbon_blue_steel": 1540,
    "high_carbon_red_steel": 1540,
    "unknown": 400
};

export const alloys = [
    {
        name: "bismuth_bronze",
        input: [
            {
                fluid: "tfc:metal/bismuth",
                nbt: {},
                amount: 20
            },
            {
                fluid: "tfc:metal/zinc",
                nbt: {},
                amount: 30
            },
            {
                fluid: "tfc:metal/copper",
                nbt: {},
                amount: 50,
            }
        ],
        result: "tfc:metal/bismuth_bronze",
        type: "heated"
    },
    {
        name: "black_bronze",
        input: [
            {
                fluid: "tfc:metal/silver",
                nbt: {},
                amount: 25
            },
            {
                fluid: "tfc:metal/gold",
                nbt: {},
                amount: 25
            },
            {
                fluid: "tfc:metal/copper",
                nbt: {},
                amount: 50
            }
        ],
        result: "tfc:metal/black_bronze",
        type: "heated"
    },
    {
        name: "brass",
        input: [
            {
                fluid: "tfc:metal/copper",
                nbt: {},
                amount: 90,
            },
            {
                fluid: "tfc:metal/zinc",
                nbt: {},
                amount: 10,
            }
        ],
        result: "tfc:metal/brass",
        type: "heated"
    },
    {
        name: "bronze",
        input: [
            {
                fluid: "tfc:metal/copper",
                nbt: {},
                amount: 90,
            },
            {
                fluid: "tfc:metal/tin",
                nbt: {},
                amount: 10,
            }
        ],
        result: "tfc:metal/bronze",
        type: "heated"
    },
    {
        name: "rose_gold",
        input: [
            {
                fluid: "tfc:metal/gold",
                nbt: {},
                amount: 70,
            },
            {
                fluid: "tfc:metal/copper",
                nbt: {},
                amount: 30,
            }
        ],
        result: "tfc:metal/rose_gold",
        type: "heated"
    },
    {
        name: "sterling_silver",
        input: [
            {
                fluid: "tfc:metal/silver",
                nbt: {},
                amount: 60,
            },
            {
                fluid: "tfc:metal/copper",
                nbt: {},
                amount: 40,
            }
        ],
        result: "tfc:metal/sterling_silver",
        type: "heated"
    },
    {
        name: "weak_blue_steel",
        input: [
            {
                fluid: "tfc:metal/black_steel",
                nbt: {},
                amount: 50,
            },
            {
                fluid: "tfc:metal/steel",
                nbt: {},
                amount: 20,
            },
            {
                fluid: "tfc:metal/bismuth_bronze",
                nbt: {},
                amount: 15,
            },
            {
                fluid: "tfc:metal/sterling_silver",
                nbt: {},
                amount: 15,
            }
        ],
        result: "tfc:metal/weak_blue_steel",
        type: "heated"
    },
    {
        name: "weak_red_steel",
        input: [
            {
                fluid: "tfc:metal/black_steel",
                nbt: {},
                amount: 50,
            },
            {
                fluid: "tfc:metal/steel",
                nbt: {},
                amount: 20,
            },
            {
                fluid: "tfc:metal/brass",
                nbt: {},
                amount: 15,
            },
            {
                fluid: "tfc:metal/rose_gold",
                nbt: {},
                amount: 15,
            }
        ],
        result: "tfc:metal/weak_red_steel",
        type: "heated"
    },
    {
        name: "weak_steel",
        input: [
            {
                fluid: "tfc:metal/steel",
                nbt: {},
                amount: 50,
            },
            {
                fluid: "tfc:metal/nickel",
                nbt: {},
                amount: 25,
            },
            {
                fluid: "tfc:metal/black_bronze",
                nbt: {},
                amount: 25,
            },
        ],
        result: "tfc:metal/weak_steel",
        type: "heated"
    }
]

export const heated_alloys = [
    {
        name: "bismuth_bronze",
        input: [
            {
                fluid: "tfc:metal/bismuth",
                nbt: {},
                amount: 20
            },
            {
                fluid: "tfc:metal/zinc",
                nbt: {},
                amount: 30
            },
            {
                fluid: "tfc:metal/copper",
                nbt: {},
                amount: 50,
            }
        ],
        result: "tfc:metal/bismuth_bronze",
        temp: 600
    },
    {
        name: "black_bronze",
        input: [
            {
                fluid: "tfc:metal/silver",
                nbt: {},
                amount: 25
            },
            {
                fluid: "tfc:metal/gold",
                nbt: {},
                amount: 25
            },
            {
                fluid: "tfc:metal/copper",
                nbt: {},
                amount: 50
            }
        ],
        result: "tfc:metal/black_bronze",
        temp: 600
    },
    {
        name: "brass",
        input: [
            {
                fluid: "tfc:metal/copper",
                nbt: {},
                amount: 90,
            },
            {
                fluid: "tfc:metal/zinc",
                nbt: {},
                amount: 10,
            }
        ],
        result: "tfc:metal/brass",
        temp: 600
    },
    {
        name: "bronze",
        input: [
            {
                fluid: "tfc:metal/copper",
                nbt: {},
                amount: 90,
            },
            {
                fluid: "tfc:metal/tin",
                nbt: {},
                amount: 10,
            }
        ],
        result: "tfc:metal/bronze",
        temp: 600
    },
    {
        name: "rose_gold",
        input: [
            {
                fluid: "tfc:metal/gold",
                nbt: {},
                amount: 70,
            },
            {
                fluid: "tfc:metal/copper",
                nbt: {},
                amount: 30,
            }
        ],
        result: "tfc:metal/rose_gold",
        temp: 600
    },
    {
        name: "sterling_silver",
        input: [
            {
                fluid: "tfc:metal/silver",
                nbt: {},
                amount: 60,
            },
            {
                fluid: "tfc:metal/copper",
                nbt: {},
                amount: 40,
            }
        ],
        result: "tfc:metal/sterling_silver",
        temp: 600
    },
    {
        name: "weak_blue_steel",
        input: [
            {
                fluid: "tfc:metal/black_steel",
                nbt: {},
                amount: 50,
            },
            {
                fluid: "tfc:metal/steel",
                nbt: {},
                amount: 20,
            },
            {
                fluid: "tfc:metal/bismuth_bronze",
                nbt: {},
                amount: 15,
            },
            {
                fluid: "tfc:metal/sterling_silver",
                nbt: {},
                amount: 15,
            }
        ],
        result: "tfc:metal/weak_blue_steel",
        temp: 600
    },
    {
        name: "weak_red_steel",
        input: [
            {
                fluid: "tfc:metal/black_steel",
                nbt: {},
                amount: 50,
            },
            {
                fluid: "tfc:metal/steel",
                nbt: {},
                amount: 20,
            },
            {
                fluid: "tfc:metal/brass",
                nbt: {},
                amount: 15,
            },
            {
                fluid: "tfc:metal/rose_gold",
                nbt: {},
                amount: 15,
            }
        ],
        result: "tfc:metal/weak_red_steel",
        temp: 600
    },
    {
        name: "weak_steel",
        input: [
            {
                fluid: "tfc:metal/steel",
                nbt: {},
                amount: 50,
            },
            {
                fluid: "tfc:metal/nickel",
                nbt: {},
                amount: 25,
            },
            {
                fluid: "tfc:metal/black_bronze",
                nbt: {},
                amount: 25,
            },
        ],
        result: "tfc:metal/weak_steel",
        temp: 600
    }
]
