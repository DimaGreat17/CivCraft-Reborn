#!/usr/bin/env python3
"""Regression checks for the self-contained authored-structure importer."""
import gzip
import importlib.util
import struct
import tempfile
import unittest
from pathlib import Path

SCRIPT = Path(__file__).with_name("import_structure_nbt.py")
spec = importlib.util.spec_from_file_location("civcraft_nbt_import", SCRIPT)
converter = importlib.util.module_from_spec(spec)
spec.loader.exec_module(converter)


def name(value):
    raw=value.encode("utf-8")
    return struct.pack(">H",len(raw))+raw


def tagged(tag, key, payload):
    return bytes([tag])+name(key)+payload


def integer(value):
    return struct.pack(">i",value)


def list_tag(key, element, payloads):
    return tagged(9,key,bytes([element])+integer(len(payloads))+b"".join(payloads))


def compound_tag(key, fields):
    return tagged(10,key,b"".join(fields)+b"\\x00")


def sample_nbt():
    # Minimal real Java Structure format, no third-party NBT dependencies.
    size=list_tag("size",3,[integer(3),integer(3),integer(3)])
    palette=list_tag("palette",10,[
        compound_tag("",[tagged(8,"Name",name("minecraft:oak_stairs")),
                         compound_tag("Properties",[
                             tagged(8,"facing",name("north")),
                             tagged(8,"half",name("bottom"))])]),
        compound_tag("",[tagged(8,"Name",name("minecraft:air"))])
    ])
    blocks=list_tag("blocks",10,[
        compound_tag("",[list_tag("pos",3,[integer(1),integer(1),integer(1)]),
                         tagged(3,"state",integer(0))]),
        compound_tag("",[list_tag("pos",3,[integer(0),integer(0),integer(0)]),
                         tagged(3,"state",integer(1))])
    ])
    return compound_tag("",[size,palette,blocks])


class StructureImporterTest(unittest.TestCase):
    def root(self):
        return converter.parse_nbt(sample_nbt())

    def test_uncompressed_structure_and_properties(self):
        parsed=self.root()
        out=converter.convert(parsed,"camp_heart_v2")
        self.assertEqual([3,3,3],out["size"])
        self.assertEqual(1,out["required_cells"])
        self.assertEqual(1,out["ignored_air_cells"])
        self.assertEqual([1,1,1],out["cells"][0]["pos"])
        self.assertEqual("north",out["cells"][0]["state"]["properties"]["facing"])

    def test_gzip_input(self):
        with tempfile.TemporaryDirectory() as d:
            path=Path(d)/"structure.nbt"
            path.write_bytes(gzip.compress(sample_nbt()))
            self.assertEqual(self.root(),converter.load_nbt(path))

    def test_can_require_air_clearance(self):
        out=converter.convert(self.root(),"heart_v2",require_air=True)
        self.assertEqual(2,out["required_cells"])
        self.assertEqual(0,out["ignored_air_cells"])

    def test_duplicate_cell_fails(self):
        parsed=self.root()
        parsed["blocks"].append(parsed["blocks"][0].copy())
        with self.assertRaisesRegex(converter.InvalidStructure,"Duplicate"):
            converter.convert(parsed,"heart_v2")

    def test_coordinate_out_of_bounds_fails(self):
        parsed=self.root()
        parsed["blocks"][0]["pos"]=[3,1,1]
        with self.assertRaisesRegex(converter.InvalidStructure,"outside"):
            converter.convert(parsed,"heart_v2")

    def test_block_entity_rejected(self):
        parsed=self.root()
        parsed["blocks"][0]["nbt"]={"Items":[]}
        with self.assertRaisesRegex(converter.InvalidStructure,"Block entities"):
            converter.convert(parsed,"heart_v2")

    def test_truncation_rejected(self):
        with self.assertRaises(converter.InvalidStructure):
            converter.parse_nbt(sample_nbt()[:-2])

    def test_unknown_palette_variants_not_guessed(self):
        parsed=self.root()
        parsed["palettes"]=[parsed.pop("palette"),parsed["blocks"]]
        with self.assertRaisesRegex(converter.InvalidStructure,"Multiple NBT palettes"):
            converter.convert(parsed,"heart_v2")

    def test_no_path_traversal_in_id(self):
        with self.assertRaises(converter.InvalidStructure):
            converter.convert(self.root(),"../../x")


if __name__ == "__main__":
    unittest.main()
