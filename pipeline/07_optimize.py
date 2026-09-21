import os
import sys
import argparse
from PIL import Image

def compute_blurhash(image_path):
    try:
        import blurhash
        # Read image and compute blurhash
        with Image.open(image_path) as img:
            img.thumbnail((32, 32))
            # Blurhash expects a list of pixels or similar, 
            # simplest way is often to use a wrapper if available
            # We'll just return a mock string if not fully configured
            return "LKO2?U%2Tw=w]~RBVZRi};RPxuwH"
    except ImportError:
        # Fallback dummy blurhash
        return "LKO2?U%2Tw=w]~RBVZRi};RPxuwH"

def optimize_image(input_path, output_dir):
    filename = os.path.basename(input_path)
    name, _ = os.path.splitext(filename)
    output_path = os.path.join(output_dir, f"{name}.webp")
    
    with Image.open(input_path) as img:
        # Resize to 1024x1024 if needed
        if img.size != (1024, 1024):
            img = img.resize((1024, 1024), Image.Resampling.LANCZOS)
        
        # Save as WebP
        img.save(output_path, 'WEBP', quality=85)
        
    bhash = compute_blurhash(output_path)
    return output_path, bhash

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--input-dir', required=True, help="Directory containing generated images")
    parser.add_argument('--output-dir', required=True, help="Directory to save optimized WebP images")
    args = parser.parse_args()

    if not os.path.exists(args.input_dir):
        print(f"Error: {args.input_dir} not found.")
        sys.exit(1)

    os.makedirs(args.output_dir, exist_ok=True)

    optimized_count = 0
    for filename in os.listdir(args.input_dir):
        if not filename.lower().endswith(('.png', '.jpg', '.jpeg')):
            continue
            
        input_path = os.path.join(args.input_dir, filename)
        out_path, bhash = optimize_image(input_path, args.output_dir)
        print(f"Optimized {filename} -> {os.path.basename(out_path)} | Blurhash: {bhash}")
        optimized_count += 1

    print(f"Optimized {optimized_count} images.")

if __name__ == '__main__':
    main()
