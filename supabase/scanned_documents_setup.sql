-- Scanner history for the GHRC app.
-- Run this once in the Supabase SQL Editor so scans follow the signed-in user
-- across phones. Files go in a private Storage bucket named "scans".

create table if not exists public.scanned_documents (
  id uuid primary key,
  user_id uuid not null references auth.users (id) on delete cascade,
  name text not null,
  type text not null,
  source text not null,
  mime_type text not null default 'image/jpeg',
  extracted_text text not null default '',
  file_size_bytes bigint not null default 0,
  page_count integer not null default 1,
  created_at bigint not null,
  storage_file_path text not null default '',
  storage_thumbnail_path text,
  is_valid boolean,
  validation_status text not null default 'NONE',
  document_kind text not null default '',
  validation_summary text not null default '',
  validation_issues text not null default '',
  is_legal boolean,
  legal_status text not null default 'NONE',
  legal_statute text not null default '',
  legal_summary text not null default '',
  legal_violations text not null default '',
  legal_missing text not null default '',
  legal_next_steps text not null default '',
  updated_at timestamptz not null default timezone('utc'::text, now())
);

create index if not exists scanned_documents_user_created_idx
  on public.scanned_documents (user_id, created_at desc);

alter table public.scanned_documents enable row level security;

drop policy if exists "Users can read own scanned documents" on public.scanned_documents;
drop policy if exists "Users can insert own scanned documents" on public.scanned_documents;
drop policy if exists "Users can update own scanned documents" on public.scanned_documents;
drop policy if exists "Users can delete own scanned documents" on public.scanned_documents;

create policy "Users can read own scanned documents"
  on public.scanned_documents
  for select
  to authenticated
  using (auth.uid() = user_id);

create policy "Users can insert own scanned documents"
  on public.scanned_documents
  for insert
  to authenticated
  with check (auth.uid() = user_id);

create policy "Users can update own scanned documents"
  on public.scanned_documents
  for update
  to authenticated
  using (auth.uid() = user_id)
  with check (auth.uid() = user_id);

create policy "Users can delete own scanned documents"
  on public.scanned_documents
  for delete
  to authenticated
  using (auth.uid() = user_id);

insert into storage.buckets (id, name, public, file_size_limit)
values ('scans', 'scans', false, 52428800)
on conflict (id) do update
set public = excluded.public,
    file_size_limit = excluded.file_size_limit;

drop policy if exists "Users can read own scan files" on storage.objects;
drop policy if exists "Users can upload own scan files" on storage.objects;
drop policy if exists "Users can update own scan files" on storage.objects;
drop policy if exists "Users can delete own scan files" on storage.objects;

create policy "Users can read own scan files"
  on storage.objects
  for select
  to authenticated
  using (
    bucket_id = 'scans'
    and split_part(name, '/', 1) = auth.uid()::text
  );

create policy "Users can upload own scan files"
  on storage.objects
  for insert
  to authenticated
  with check (
    bucket_id = 'scans'
    and split_part(name, '/', 1) = auth.uid()::text
  );

create policy "Users can update own scan files"
  on storage.objects
  for update
  to authenticated
  using (
    bucket_id = 'scans'
    and split_part(name, '/', 1) = auth.uid()::text
  )
  with check (
    bucket_id = 'scans'
    and split_part(name, '/', 1) = auth.uid()::text
  );

create policy "Users can delete own scan files"
  on storage.objects
  for delete
  to authenticated
  using (
    bucket_id = 'scans'
    and split_part(name, '/', 1) = auth.uid()::text
  );
