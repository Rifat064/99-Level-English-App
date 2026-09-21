import csv
import sys
import os
import argparse

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--mock', action='store_true', help="Use mock Supabase upload")
    args = parser.parse_args()

    input_file = '05_review.csv'
    image_dir = 'images/optimized'

    if not os.path.exists(input_file):
        print(f"Error: {input_file} not found.")
        sys.exit(1)

    rows = []
    with open(input_file, 'r', encoding='utf-8') as f:
        reader = csv.DictReader(f)
        for row in reader:
            rows.append(row)

    print(f"Loaded {len(rows)} rows for upload.")

    if not args.mock:
        from supabase import create_client, Client
        
        url: str = os.environ.get("SUPABASE_URL")
        key: str = os.environ.get("SUPABASE_SERVICE_ROLE_KEY") or os.environ.get("SUPABASE_ANON_KEY")
        
        if not url or not key:
            print("Error: SUPABASE_URL and key must be set in environment for real upload.")
            sys.exit(1)
            
        supabase: Client = create_client(url, key)

    uploaded_count = 0
    for i, row in enumerate(rows):
        image_name = f"card_{i+1:03d}.webp"
        image_path = os.path.join(image_dir, image_name)
        
        storage_key = f"season1/{image_name}"

        # 1. Upload to storage
        if args.mock:
            # print(f"[MOCK] Uploaded {image_name} to bucket 'card-images' as {storage_key}")
            pass
        else:
            if not os.path.exists(image_path):
                print(f"Error: optimized image {image_path} not found.")
                sys.exit(1)
            with open(image_path, 'rb') as f:
                res = supabase.storage.from_("card-images").upload(file=f, path=storage_key, file_options={"content-type": "image/webp"})

        # 2. Insert to DB
        # Note: We need word_a_id and word_b_id which are foreign keys. 
        # For the mock, we'll just assume they exist or we'll bypass the strict FK check in the mock log.
        card_data = {
            'day_index': i + 1,
            'season': 1,
            'sentence_en': row['sentence_en'],
            'sentence_bn': row['sentence_bn'],
            'image_path': storage_key,
            'image_blurhash': "LKO2?U%2Tw=w]~RBVZRi};RPxuwH",
            'status': 'reviewed'
        }

        if args.mock:
            # print(f"[MOCK] Inserted card: {card_data}")
            pass
        else:
            data, count = supabase.table('cards').insert(card_data).execute()

        uploaded_count += 1

    if args.mock:
        print(f"[MOCK] Successfully processed and 'uploaded' {uploaded_count} cards.")
    else:
        print(f"Successfully uploaded {uploaded_count} images and inserted {uploaded_count} rows into cards table.")

if __name__ == '__main__':
    main()
