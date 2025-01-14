export const nuggets = [
    {
        "name": "native_copper",
        "result": "copper"
    },
    {
        "name": "native_gold",
        "result": "gold"
    },
    {
        "name": "hematite",
        "result": "cast_iron"
    },
    {
        "name": "native_silver",
        "result": "silver"
    },
    {
        "name": "cassiterite",
        "result": "tin"
    },
    {
        "name": "bismuthinite",
        "result": "bismuth"
    },
    {
        "name": "garnierite",
        "result": "nickel"
    },
    {
        "name": "malachite",
        "result": "copper"
    },
    {
        "name": "magnetite",
        "result": "cast_iron"
    },
    {
        "name": "limonite",
        "result": "cast_iron"
    },
    {
        "name": "sphalerite",
        "result": "zinc"
    },
    {
        "name": "tetrahedrite",
        "result": "copper"
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
    "bismuth",
    "bismuth_bronze",
    "black_bronze",
    "bronze",
    "brass",
    "copper",
    "gold",
    "nickel",
    "rose_gold",
    "silver",
    "tin",
    "zinc",
    "sterling_silver",
    "wrought_iron",
    "cast_iron",
    "pig_iron",
    "steel",
    "black_steel",
    "blue_steel",
    "red_steel",
    "weak_steel",
    "weak_blue_steel",
    "weak_red_steel",
    "high_carbon_steel",
    "high_carbon_black_steel",
    "high_carbon_blue_steel",
    "high_carbon_red_steel",
    "unknown"
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
