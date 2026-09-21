import csv
import sys
import os
import argparse
from PIL import Image

def generate_mock_image(output_path, text):
    # Generates a solid color image for mock
    img = Image.new('RGB', (1024, 1024), color = (73, 109, 137))
    img.save(output_path)

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--mock', action='store_true', help="Use mock image generation")
    args = parser.parse_args()

    input_file = '05_review.csv'
    output_dir = 'images/raw'

    if not os.path.exists(input_file):
        print(f"Error: {input_file} not found.")
        sys.exit(1)

    os.makedirs(output_dir, exist_ok=True)

    rows = []
    with open(input_file, 'r', encoding='utf-8') as f:
        reader = csv.DictReader(f)
        for row in reader:
            rows.append(row)

    generated_count = 0
    for i, row in enumerate(rows):
        image_name = f"card_{i+1:03d}.png"
        output_path = os.path.join(output_dir, image_name)

        if args.mock:
            generate_mock_image(output_path, row['image_prompt'])
        else:
            print("Real Image Generation not fully configured, falling back to mock.")
            generate_mock_image(output_path, row['image_prompt'])
        
        # We might want to save the image name in the CSV or just rely on index
        # For this pipeline, 08_upload.py will just pair by index
        generated_count += 1
        if generated_count % 10 == 0:
            print(f"Generated {generated_count} images...")

    print(f"Successfully generated {generated_count} images into {output_dir}.")

if __name__ == '__main__':
    main()
